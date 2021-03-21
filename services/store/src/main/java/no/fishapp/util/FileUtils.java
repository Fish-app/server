package no.fishapp.app.util;

import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.nio.file.Files;
import java.security.KeyPair;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtils {

    public static final File executionDir = new File("").getAbsoluteFile();
    public static final File systemTmpDir = new File(System.getProperty("java.io.tmpdir"));

    /**
     * Cheks whther or not the given file is a child of the given directory.
     * if ether is null false is returned
     *
     * @param file   The file to check if is in the directory
     * @param parent The dir to check if the file is in
     * @return true if the file is in any of the dirs sub directorys
     */
    public static boolean isFileChildOfDir(File file, File parent) {
        boolean isChild = false;
        try {
            if (file.getCanonicalPath().startsWith(parent.getCanonicalPath() + File.separator)) {
                isChild = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isChild;
    }

    /**
     * Deletes a directory recursivly, symbolic links are not followed
     *
     * @param file The dir to delete.
     */
    public static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }

    /**
     * Utility method for returning a file with one less ending than the one provided, that is:
     * File(/bip/bop/abc.tar.gz) -> File(/bip/bop/abc.tar)
     * <p>
     * If the file provided have no ending the input object is returned
     *
     * @param file the file to remove the ending from
     * @return the file object representing a file with one less ending
     */
    public static File getFileWithOneLessEnding(File file) {
        int lastDot = file.getAbsolutePath().lastIndexOf(".");
        if (lastDot > 0) {
            return new File(file.getAbsolutePath().substring(0, lastDot));
        } else {
            return file;
        }
    }

    /**
     * Utility method for getting the file ending of the provided file.
     * If no ending is found null is returned.
     * The dot is included so abc.jpg yields .jpg and abc yields null.
     *
     * @param file the file to get the file extention from
     *
     * @return the file extension to the file at the provided path inclusive the dot null if no extension.
     */
    public static String getFileExtension(File file){
        return getFilePathExtension(file.getAbsolutePath());
    }

    /**
     * Utility method for getting the file ending of the provided file path.
     * If no ending is found null is returned.
     * The dot is included so abc.jpg yields .jpg and abc yields null.
     *
     * @param filePath the filepath to get the extension from.
     *
     * @return the file extension to the file at the provided path inclusive the dot null if no extension.
     */
    public static String getFilePathExtension(String filePath) {
        int lastDot = filePath.lastIndexOf(".");
        if (lastDot > 0) {
            return filePath.substring(lastDot);
        } else {
            return null;
        }
    }

    /**
     * Tries to deserialize the provided file in to the provided objet type. Try to find a better solution.
     * this is not ideal.
     *
     * @param file the file to deserialize from.
     * @param <T>  the type to deserialize in to.
     *
     * @return the deserialized objet
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <T> T deserializeObjetFromFile(File file) throws IOException, ClassNotFoundException {

        ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));

        return (T) objectInputStream.readObject();

    }

    /**
     * Serializes the provided objet to the provided file.
     *
     * @param object the objet to serialize.
     * @param file   the file to serialize to.
     *
     * @throws IOException
     */
    public static void serializeObjetToFile(Object object, String file) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(
                file)));
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
    }
}
