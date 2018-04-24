/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */
package utybo.branchingstorytree.swing.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import utybo.branchingstorytree.api.BSTException;
import utybo.branchingstorytree.api.BranchingStoryTreeParser;
import utybo.branchingstorytree.api.script.Dictionary;
import utybo.branchingstorytree.api.story.BranchingStory;
import utybo.branchingstorytree.swing.impl.TabClient;
import utybo.branchingstorytree.swing.virtualfiles.BRMVirtualFileClient;
import utybo.branchingstorytree.swing.virtualfiles.VirtualFile;
import utybo.branchingstorytree.swing.virtualfiles.VirtualFileHolder;
import utybo.branchingstorytree.swing.visuals.AccumulativeRunnable;

public class BSTPackager
{
    @SuppressFBWarnings(value = "DM_DEFAULT_ENCODING")
    public static void main(String[] args) throws Exception
    {
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);
        boolean b = true;
        while(b)
        {

            System.out.println("What do you wish to package today?");
            File f = new File(sc.nextLine());
            if(!f.exists())
            {
                System.out.println("Doesn't exist!");
                continue;
            }
            File outFile = new File(f.getParent(), "PACKAGED.bsp");
            if(outFile.exists())
            {
                if(!outFile.delete())
                {
                    throw new RuntimeException("Cannot delete already existing file");
                }
            }
            if(!outFile.createNewFile())
            {
                throw new RuntimeException("Failed to create file");
            }
            System.out.println("Packaging...");
            toPackage(f, new FileOutputStream(outFile), new HashMap<>(),
                    s -> System.out.println(s));
            System.out.println("Done");
            b = false;
        }
        sc.close();
    }

    public static void toPackage(File bstFile, OutputStream out, HashMap<String, String> meta)
            throws IOException
    {
        toPackage(bstFile, out, meta, null);
    }

    public static void toPackage(File bstFile, OutputStream out, HashMap<String, String> meta,
            Consumer<String> cs) throws IOException
    {
        TarArchiveOutputStream tar = new TarArchiveOutputStream(new GZIPOutputStream(out));

        if(cs != null)
        {
            cs.accept("Packaging the main BST file");
        }
        // Write the main BST file
        tarFile(bstFile, tar);

        // Write the resources folder
        if(cs != null)
        {
            cs.accept("Packaging resources");
        }
        tarFolder(new File(bstFile.getParentFile(), "resources"), "resources", tar, cs);

        // Write the meta file
        if(cs != null)
        {
            cs.accept("Writing meta information");
        }
        meta.put("mainFile", bstFile.getName());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
        new Gson().toJson(meta, osw);
        osw.flush();
        osw.close();
        TarArchiveEntry tae = new TarArchiveEntry("bstmeta.json");
        tae.setSize(baos.size());
        InputStream bais = baos.toInputStream();
        tar.putArchiveEntry(tae);
        IOUtils.copy(bais, tar);
        tar.closeArchiveEntry();
        tar.close();

        if(cs != null)
        {
            cs.accept("Done");
        }
    }

    public static void embed(File toEmbed, File openbstJar, File splashscreen, File output,
            Consumer<String> callback) throws IOException
    {

        callback.accept("Checking");
        if(!openbstJar.getAbsolutePath().endsWith(".jar"))
            throw new IOException("Not a valid OpenBST jar file");

        callback.accept("Processing BST/BSP file...");
        File sourceFile;
        if(toEmbed.getAbsolutePath().endsWith(".bsp"))
        {
            sourceFile = toEmbed;
        }
        else
        {
            if(toEmbed.getAbsolutePath().endsWith(".bst"))
            {
                callback.accept("Converting from BST to BSP");
                sourceFile = File.createTempFile("openbst", ".bsp");
                FileOutputStream fos = new FileOutputStream(sourceFile);
                toPackage(toEmbed, fos, new HashMap<>(), callback);
                fos.close();
            }
            else
                throw new IOException("toEmbed file is not a BSP or BST file.");
        }

        callback.accept("Copying original OpenBST JAR");
        if(output.exists())
            output.delete();
        FileUtils.copyFile(openbstJar, output);

        callback.accept("Embedding");
        FileSystem zipfs = FileSystems.newFileSystem(output.toPath(), null);
        Files.copy(sourceFile.toPath(), zipfs.getPath("/embed.bsp"),
                StandardCopyOption.REPLACE_EXISTING);
        if(splashscreen != null)
            Files.copy(splashscreen.toPath(), zipfs.getPath("/splashscreen.png"),
                    StandardCopyOption.REPLACE_EXISTING);
        zipfs.close();
        callback.accept("Done");
    }

    private static void tarFile(File file, TarArchiveOutputStream tar) throws IOException
    {
        tarFile(file, file.getName(), tar, null);
    }

    private static void tarFile(File file, String name, TarArchiveOutputStream tar,
            Consumer<String> cs) throws IOException
    {
        if(cs != null)
        {
            cs.accept("Packaging " + file.getName());
        }
        TarArchiveEntry entry = new TarArchiveEntry(name);
        entry.setSize(file.length());
        tar.putArchiveEntry(entry);
        FileInputStream fis = new FileInputStream(file);
        IOUtils.copy(fis, tar);
        fis.close();
        tar.closeArchiveEntry();
    }

    private static void tarFolder(File folder, String base, TarArchiveOutputStream tar,
            Consumer<String> cs) throws IOException
    {
        if(cs != null)
        {
            cs.accept("Packaging folder " + folder.getName());
        }
        if(!folder.exists() || !folder.isDirectory())
        {
            return;
        }
        else
        {
            File[] fl = folder.listFiles();
            assert fl != null;
            for(File f : fl)
            {
                if(f.isDirectory())
                {
                    tarFolder(f, base + "/" + f.getName(), tar, cs);
                    continue;
                }
                tarFile(f, base + "/" + f.getName(), tar, cs);
            }
        }
    }

    public static BranchingStory fromPackage(InputStream in, TabClient client)
            throws IOException, BSTException, InstantiationException, IllegalAccessException
    {
        return fromPackage(in, client, null);
    }

    public static BranchingStory fromPackage(InputStream in, TabClient client,
            Consumer<String> consumer)
            throws IOException, BSTException, InstantiationException, IllegalAccessException
    {
        if(consumer == null)
            consumer = s ->
            {};
        Consumer<String> c = consumer;
        AccumulativeRunnable<String> run = new AccumulativeRunnable<String>()
        {

            @Override
            public void run(List<String> retrieveObjects)
            {
                c.accept(retrieveObjects.get(retrieveObjects.size() - 1));
            }
        };
        run.add("Loading BSP file");
        TarArchiveInputStream tais = new TarArchiveInputStream(new GZIPInputStream(in));
        VirtualFileHolder vfh = new VirtualFileHolder();
        TarArchiveEntry tae;
        while((tae = tais.getNextTarEntry()) != null)
        {
            run.add("Loading " + tae.getName());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copyLarge(tais, baos, 0, tae.getSize());
            vfh.add(new VirtualFile(baos.toByteArray(), tae.getName()));
        }

        run.add("Reading metadata");
        HashMap<String, String> meta = new Gson().fromJson(new InputStreamReader(
                new ByteArrayInputStream(vfh.getFile("bstmeta.json").getData()),
                StandardCharsets.UTF_8), new TypeToken<HashMap<String, String>>()
                {}.getType());
        run.add("Parsing story");
        BranchingStoryTreeParser parser = new BranchingStoryTreeParser();
        BranchingStory bs = parser.parse(new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(vfh.getFile(meta.get("mainFile")).getData()),
                StandardCharsets.UTF_8)), new Dictionary(), client, "<main>");
        client.setBRMHandler(new BRMVirtualFileClient(vfh, client, bs));

        return bs;
    }
}
