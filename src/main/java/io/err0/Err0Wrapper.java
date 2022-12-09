package io.err0;

import com.google.gson.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;

public class Err0Wrapper {
    public Err0Wrapper(final BaseMojo mojo) {
        this.mojo = mojo;
    }
    final BaseMojo mojo;

    public void call(final String args[]) throws MojoExecutionException {
        final Log log = mojo.getLog();
        //log.info("GET https://api.github.com/repos/Err0-io/err0agent/releases/latest");

        OkHttpClient client = new OkHttpClient.Builder().build();

        String jar = null, checksum = null;
        try {
            Request request = new Request.Builder()
                    .url("https://api.github.com/repos/Err0-io/err0agent/releases/latest")
                    .get()
                    .build();
            Call call = client.newCall(request);
            Response response = call.execute();
            JsonObject latestAgent = JsonParser.parseString(response.body().string()).getAsJsonObject();
            JsonArray assets = latestAgent.getAsJsonArray("assets");
            for (int i = 0, l = assets.size(); i < l; ++i) {
                JsonObject asset = assets.get(i).getAsJsonObject();
                String filename = asset.get("name").getAsString();
                String url = asset.get("browser_download_url").getAsString();
                if (filename.endsWith(".jar")) {
                    jar = url;
                }
                else if (filename.endsWith(".jar.sha256")) {
                    checksum = url;
                }
            }

            if (null == jar) {
                throw new MojoExecutionException("Unable to locate jar in latest release.");
            }
            if (null == checksum) {
                throw new MojoExecutionException("Unable to locate checksum in latest release.");
            }

            //log.info("err0agent jar: " + jar);
            //log.info("err0agent checksum: " + checksum);

        } catch (IOException e) {
            throw new MojoExecutionException("Checking latest err0agent release.", e);
        }

        String checksumValue = null;
        try {
            Request request = new Request.Builder()
                    .url(checksum)
                    .get()
                    .build();
            Call call = client.newCall(request);
            Response response = call.execute();

            checksumValue = response.body().string();
            int i = checksumValue.indexOf(' ');
            if (i < 0) {
                throw new MojoExecutionException("Unexpected checksum format");
            }
            checksumValue = checksumValue.substring(0, i);

            //log.info("Checksum = " + checksumValue);
        } catch (IOException e) {
            throw new MojoExecutionException("Downloading err0agent's checksum.", e);
        }

        boolean download = true;
        try {
            File f = new File(mojo.getErr0AgentJar());
            if (f.exists()) {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] encodedhash = digest.digest(Files.readAllBytes(f.toPath()));
                StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
                for (int i = 0; i < encodedhash.length; i++) {
                    String hex = Integer.toHexString(0xff & encodedhash[i]);
                    if(hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
                String currentAgentChecksumValue = hexString.toString();

                if (! checksumValue.equals(currentAgentChecksumValue)) {
                    log.info("Current agent is out of date, needs to be refreshed.");
                } else {
                    download = false;
                }
            }
        }
        catch (IOException | NoSuchAlgorithmException e) {
            throw new MojoExecutionException("Checking current agent version.", e);
        }

        if (download) {
            log.info("Need to download agent");
        } else {
            log.info("Got current agent");
        }

        if (download) {
            try {
                Request request = new Request.Builder()
                        .url(jar)
                        .get()
                        .build();
                Call call = client.newCall(request);
                Response response = call.execute();

                byte[] agentJarData = response.body().bytes();

                // check checksum before writing
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] encodedhash = digest.digest(agentJarData);
                StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
                for (int i = 0; i < encodedhash.length; i++) {
                    String hex = Integer.toHexString(0xff & encodedhash[i]);
                    if(hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
                String downloadedAgentChecksumValue = hexString.toString();
                if (checksumValue.equals(downloadedAgentChecksumValue)) {
                    Files.write(new File(mojo.getErr0AgentJar()).toPath(), agentJarData, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
                } else {
                    throw new MojoExecutionException("Downloaded jar failed checksum verification.");
                }

            } catch (IOException | NoSuchAlgorithmException e) {
                throw new MojoExecutionException("Downloading err0agent binary.", e);
            }
        }

        StringBuilder commandLine = new StringBuilder("java -jar " + mojo.getErr0AgentJar());
        for (int i = 0; i < args.length; ) {
            commandLine.append(' ').append(args[i++]);
        }
        String command = commandLine.toString();
        log.info(command);

        try {

            StringBuilder builder = new StringBuilder();

            Process process = Runtime.getRuntime().exec(command);
            Executors.newSingleThreadExecutor().submit(() ->
                    new BufferedReader(new InputStreamReader(process.getInputStream()))
                            .lines().forEach(line -> { builder.append(line).append("\r\n"); })
            );
            Executors.newSingleThreadExecutor().submit(() ->
                    new BufferedReader(new InputStreamReader(process.getErrorStream()))
                            .lines().forEach(line -> { builder.append(line).append("\r\n"); })
            );
            int exitCode = process.waitFor();

            log.info(builder.toString());

            if (exitCode != 0) {
                throw new MojoExecutionException("Err0 pass failed.");
            }
        }
        catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("Executing err0agent binary.", e);
        }

    }
}
