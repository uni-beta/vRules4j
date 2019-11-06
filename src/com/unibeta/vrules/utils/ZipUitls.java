package com.unibeta.vrules.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

public class ZipUitls {

	private static final String FILE_WAR = ".war";
	private static final String FILE_JAR = ".jar";
	private static final String FILE_ZIP = ".zip";
	private static final Project DEFAULT_PROJECT = new Project();
	private static Logger log = Logger.getLogger(ZipUitls.class);

	/**
	 * zip given files to zip file
	 * 
	 * @param inputFile
	 * @return zipped file
	 * @throws Exception
	 */
	public static File zip(File inputFile) throws Exception {

		String name = inputFile.getName();
		int lastIndexOf = name.lastIndexOf(".");

		String zipName = lastIndexOf > 0 ? name.substring(0, lastIndexOf) : name;
		String zipFileName = inputFile.getParent() + "/" + zipName + FILE_ZIP;
		File zipFile = new File(zipFileName);

		try {
			Class.forName("org.apache.tools.ant.taskdefs.Zip");
			zip(inputFile, zipFile);
		} catch (Exception e) {
			log.warn(e.getMessage() + ", try to use local zip engine.");
			zip0(inputFile, zipFile);
		}

		return new File(zipFileName);
	}

	public static void zip(File srcFile, File destFile) {

		Zip zip = new Zip();

		zip.setProject(DEFAULT_PROJECT);
		zip.setDestFile(destFile);
		zip.setEncoding("UTF-8");
		// zip.setBasedir(srcFile);
		zip.setCaseSensitive(true);

		FileSet fs = new FileSet();
		fs.setProject(DEFAULT_PROJECT);
		fs.setDir(srcFile.getParentFile());

		if (srcFile.isDirectory()) {
			fs.setIncludes(srcFile.getName() + "/**");
		} else {
			fs.setIncludes(srcFile.getName());
		}
		// fs.setExcludes("**/*.xml");

		zip.addFileset(fs);
		zip.execute();

	}

	/**
	 * unzip file to local folder. only zip type is supported.
	 * 
	 * @param file
	 * @throws Exception
	 */
	public static String unzip(File file) throws Exception {

		String outfile = file.getCanonicalPath();

		if (!outfile.toLowerCase().endsWith(FILE_ZIP) && !outfile.toLowerCase().endsWith(FILE_JAR)
				&& !outfile.toLowerCase().endsWith(FILE_WAR)) {
			throw new Exception("file format is not supported.");
		}

		outfile = outfile.substring(0, (outfile.length() - FILE_ZIP.length()));

		try {
			Class.forName("org.apache.tools.ant.taskdefs.Expand");
			unzip(file, file.getParentFile());
		} catch (Exception e) {
			log.warn(e.getMessage() + ", try to use local unzip engine.");
			unzip0(file);
		}

		return outfile;
	}

	public static void unzip(File srcFile, File destFile) {
		Expand expand = new Expand();

		expand.setEncoding("UTF-8");
		expand.setProject(DEFAULT_PROJECT);
		expand.setSrc(srcFile);
		expand.setDest(destFile);
		expand.setOverwrite(true);

		expand.execute();

	}

	private static void zip0(File inputFile, File zipFileName) throws FileNotFoundException, Exception, IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));

		zip(out, inputFile, inputFile.getName());

		out.close();
	}

	static private void zip(ZipOutputStream out, File f, String base) throws Exception {

		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			if (fl.length == 0) {
				out.putNextEntry(new ZipEntry(base + "/"));
			}
			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + "/" + fl[i].getName());
			}
		} else {
			ZipEntry zipEntry = new ZipEntry(base);
			out.putNextEntry(zipEntry);
			FileInputStream in = new FileInputStream(f);
			BufferedInputStream bi = new BufferedInputStream(in);

			byte[] b = new byte[(int) f.length()];

			bi.read(b);
			out.write(b);

			bi.close();
			in.close();
		}
	}

	private static void unzip0(File file) throws FileNotFoundException, ZipException, IOException {
		String parentPath = file.getParent() + File.separator;

		FileInputStream fileInputStream = new FileInputStream(file);
		ZipFile zipFile = new ZipFile(file);
		Enumeration e = zipFile.entries();

		while (e.hasMoreElements()) {
			ZipEntry ze = (ZipEntry) e.nextElement();

			if (!ze.isDirectory()) {
				InputStream is = zipFile.getInputStream(ze);
				String zipedFile = parentPath + ze.getName();
				checkFile(zipedFile);

				FileOutputStream fileOutputStream = new FileOutputStream(zipedFile);

				byte[] b = new byte[(int) ze.getSize()];

				is.read(b);
				fileOutputStream.write(b);

				is.close();
				fileInputStream.close();
			}
		}

		zipFile.close();
	}

	/**
	 * Check file whether it is valid, if it does not exist, create it
	 * automatically.
	 * 
	 * @param file
	 */
	static void checkFile(String filePath) {

		File file = new File(filePath);

		if (null != file.getParent()) {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
		}
	}
	
	/**
	 * Delete all files under given file name or folder name.
	 * 
	 * @param fileName
	 */
	public static void deleteFiles(String fileName) {

		if (CommonUtils.isNullOrEmpty(fileName)) {
			return;
		}

		File file = new File(fileName);

		if (file.isDirectory()) {
			File[] files = file.listFiles();

			if (null != files) {
				for (File f : files) {
					if (f.isDirectory()) {
						deleteFiles(f.getPath());
						f.delete();
					} else {
						f.delete();
					}
				}
			}
		} else {
			file.delete();
		}

	}

}
