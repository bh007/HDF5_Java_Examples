/*****************************************************************************
 * Copyright by The HDF Group.                                               *
 * Copyright by the Board of Trustees of the University of Illinois.         *
 * All rights reserved.                                                      *
 *                                                                           *
 * This file is part of the HDF Java Products distribution.                  *
 * The full copyright notice, including terms governing use, modification,   *
 * and redistribution, is contained in the files COPYING and Copyright.html. *
 * COPYING can be found at the root of the source code distribution tree.    *
 * Or, see http://hdfgroup.org/products/hdf-java/doc/Copyright.html.         *
 * If you do not have access to either file, you may request a copy from     *
 * help@hdfgroup.org.                                                        *
 ****************************************************************************/

package object.hdf5;

import hdf.object.Dataset;
import hdf.object.Datatype;
import hdf.object.FileFormat;
import hdf.object.Group;
import hdf.object.h5.H5File;

/**
 * <p>
 * Title: HDF Object Package (Java) Example
 * </p>
 * <p>
 * Description: this example shows how to create HDF5 datasets using the
 * "HDF Object Package (Java)". The example created the group structure and
 * datasets:
 * 
 * <pre>
 *     "/" (root)
 *         integer arrays
 *             2D 32-bit integer 20x10
 *             3D 16-bit integer 20x10x5
 *         float arrays
 *             2D 64-bit double 20x10
 *             3D 32-bit float  20x10x5
 * </pre>
 * 
 * </p>
 */
public class H5DatasetCreate {
    private static String fname  = "H5DatasetCreate.h5";
    private static long[] dims2D = { 20, 10 };
    private static long[] dims3D = { 20, 10, 5 };

    public static void main(String args[]) throws Exception {
        // retrieve an instance of H5File
        FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);

        if (fileFormat == null) {
            System.err.println("Cannot find HDF5 FileFormat.");
            return;
        }

        // create a new file with a given file name.
        H5File testFile = (H5File) fileFormat.createFile(fname, FileFormat.FILE_CREATE_DELETE);

        if (testFile == null) {
            System.err.println("Failed to create file:" + fname);
            return;
        }

        // open the file and retrieve the root group
        testFile.open();
        Group root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) testFile.getRootNode()).getUserObject();

        // create groups at the root
        Group g1 = testFile.createGroup("integer arrays", root);
        Group g2 = testFile.createGroup("float arrays", root);

        // create 2D 32-bit (4 bytes) integer dataset of 20 by 10
        Datatype dtype = testFile.createDatatype(Datatype.CLASS_INTEGER, 4, Datatype.NATIVE, Datatype.NATIVE);
        Dataset dataset = testFile.createScalarDS("2D 32-bit integer 20x10", g1, dtype, dims2D, null, null, 0, null);

        // create 3D 8-bit (1 byte) unsigned integer dataset of 20 by 10 by 5
        dtype = testFile.createDatatype(Datatype.CLASS_INTEGER, 1, Datatype.NATIVE, Datatype.SIGN_NONE);
        dataset = testFile.createScalarDS("3D 8-bit unsigned integer 20x10x5", g1, dtype, dims3D, null, null, 0, null);

        // create 2D 64-bit (8 bytes) double dataset of 20 by 10
        dtype = testFile.createDatatype(Datatype.CLASS_FLOAT, 8, Datatype.NATIVE, -1);
        dataset = testFile.createScalarDS("2D 64-bit double 20x10", g2, dtype, dims2D, null, null, 0, null);

        // create 3D 32-bit (4 bytes) float dataset of 20 by 10 by 5
        dtype = testFile.createDatatype(Datatype.CLASS_FLOAT, 4, Datatype.NATIVE, -1);
        dataset = testFile.createScalarDS("3D 32-bit float  20x10x5", g2, dtype, dims3D, null, null, 0, null);

        // close file resource
        testFile.close();
    }
}
