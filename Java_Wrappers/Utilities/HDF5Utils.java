package Utilities;

import java.util.*;

import hdf.hdf5lib.H5;
import hdf.hdf5lib.HDF5Constants;
import hdf.hdf5lib.structs.H5L_info_t;
import hdf.hdf5lib.structs.H5O_info_t;
import hdf.hdf5lib.structs.H5A_info_t;
import hdf.hdf5lib.structs.H5G_info_t;

public class HDF5Utils {

	private static String[] FILENAME  = { "Ar3_BEPS_2VAC_20VDC_0003.h5",
										  "KNN_BE_Line_1um_0002.h5"
										};
	private static String[] DATASETNAME = { "/Measurement_000/Channel_000/Raw_Data",
											"/Measurement_000/Channel_000/Raw_Data-SHO_Fit_000",
											"/Measurement_000/Channel_000/Spatially_Averaged_Plot_Group_000",
											"/Measurement_000/Channel_000/Raw_Data-SHO_Fit_000/Fit",
											"/Measurement_000/Channel_000/Bin_Indices",
											"/Measurement_000/Channel_000/Noise_Floor",
											"/Measurement_000/Channel_000/Position_Indices",
											"/Measurement_000/Channel_000/Bin_FFT"
										  };

	public static LinkedHashMap<String, String> getLinkInfo ( String fileName, String linkName ) throws Exception {
		
		int file_id = -1;
		
		try {
            file_id = H5.H5Fopen(fileName,
								 HDF5Constants.H5F_ACC_RDONLY,
								 HDF5Constants.H5P_DEFAULT);
		} catch (Exception e) { e.printStackTrace();  System.exit(1); }
		
		H5L_info_t linkInfo = null;
		LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();

		if( H5.H5Lexists(file_id, linkName, HDF5Constants.H5P_DEFAULT) ) {
			try {
				linkInfo = H5.H5Lget_info(file_id, 
										  linkName,
										  HDF5Constants.H5P_DEFAULT);
			} catch (Exception e) { e.printStackTrace(); }
			
			info.put( "File", fileName);
			info.put( "Name", linkName);
			info.put( "Class", "Link");
			info.put( "Address", Long.toString(linkInfo.address_val_size) );
			switch (linkInfo.type) {
				case 0:	info.put( "Type", "Hard" ); break;
				case 1:	info.put( "Type", "Soft" ); break;
				case 2:	info.put( "Type", "External" ); break;
				case 3: info.put( "Type", "Error" ); break;
				default: break;
			}
			switch (linkInfo.cset) {
				case 0:	info.put( "Character_Set", "US_ASCII" ); break;
				case 1:	info.put( "Character_Set", "UTF-8" ); break;
				default: break;
			}
			info.put( "Corder_	Valid", Boolean.toString(linkInfo.corder_valid) );
			info.put( "Corder", Long.toString(linkInfo.corder) );
		} else {
			System.out.println( "\"" + linkName +  "\" doesn't exist in file \"" + fileName + "\"." );
		}
		
		try {
			if (file_id >= 0) H5.H5Fclose(file_id);
		} catch (Exception e) { e.printStackTrace(); }
		
		return info;
	}
	
	public static LinkedHashMap<String, String> getObjectInfo ( String fileName, String linkName ) throws Exception {
		
		int file_id = -1;
		
		try {
            file_id = H5.H5Fopen(fileName,
								 HDF5Constants.H5F_ACC_RDONLY,
								 HDF5Constants.H5P_DEFAULT);
		} catch (Exception e) { e.printStackTrace();  System.exit(1); }
		
		H5O_info_t objectInfo = null;
		LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();

		if ( H5.H5Lexists(file_id, linkName, HDF5Constants.H5P_DEFAULT) ) {
			try {
				objectInfo = H5.H5Oget_info_by_name(file_id,
													linkName,
													HDF5Constants.H5P_DEFAULT);
			} catch (Exception e) { e.printStackTrace(); }
			
			info.put( "File", fileName);
			info.put( "Name", linkName);
			info.put( "Class", "Object");
			info.put( "Address", Long.toString(objectInfo.addr) );
			switch (objectInfo.type) {
				case -1: info.put( "Type", "Unknown" ); break;
				case  0: info.put( "Type", "Datagroup" ); break;
				case  1: info.put( "Type", "Dataset" ); break;
				case  2: info.put( "Type", "Committed_(Named)_Datatype" ); break;
				default: break;
			}
			info.put( "Number_Attributes", Long.toString(objectInfo.num_attrs) );
			info.put( "Reference count", Integer.toString(objectInfo.rc) );
			info.put( "File_Number", Long.toString(objectInfo.fileno) );
			info.put( "Access_Time", Long.toString(objectInfo.atime) );
			info.put( "Birth_Time", Long.toString(objectInfo.btime) );
			info.put( "Change_Time", Long.toString(objectInfo.ctime) );
			info.put( "Modification_Time", Long.toString(objectInfo.mtime) );
		} else {
			System.out.println( "\"" + linkName +  "\" doesn't exist in file \"" + fileName + "\"." );
		}
		
		try {
			if (file_id >= 0) H5.H5Fclose(file_id);
		} catch (Exception e) { e.printStackTrace(); }
		
		return info;
	}
	
	public static LinkedHashMap<String, String> getDatasetInfo ( String fileName, String linkName ) throws Exception {
		
		int file_id = -1;
		
		try {
            file_id = H5.H5Fopen(fileName,
								 HDF5Constants.H5F_ACC_RDONLY,
								 HDF5Constants.H5P_DEFAULT);
		} catch (Exception e) { e.printStackTrace();  System.exit(1); }
		
		H5O_info_t objectInfo = null;
		int dataset_id = -1;
		int dataspace_id = -1;
		int datatype_id = -1;
		LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
		
		if ( H5.H5Lexists(file_id, linkName, HDF5Constants.H5P_DEFAULT) ) {
			
			try {
				objectInfo = H5.H5Oget_info_by_name(file_id,
													linkName,
													HDF5Constants.H5P_DEFAULT);
			} catch (Exception e) { e.printStackTrace(); }
				
			if( objectInfo.type == 1 ) {
				try {
					dataset_id = H5.H5Dopen(file_id,
										    linkName,
										    HDF5Constants.H5P_DEFAULT);
				} catch (Exception e) { e.printStackTrace(); }

				info.put( "File", fileName);
				info.put( "Name", linkName);
				info.put( "Type", "Dataset");
				info.put( "Dataset_ID", Integer.toString(dataset_id) );

				dataspace_id = H5.H5Dget_space(dataset_id);
				info.put( "Dataspace_ID", Integer.toString(dataspace_id) );
				
				int dataspace_class = H5.H5Sget_simple_extent_type(dataspace_id);
				switch (dataspace_class) {
					case 0:
						info.put( "Dataspace_Class", "H5S_SCALAR" );
					break;
					
					case 1:
						info.put( "Dataspace_Class", "H5S_SIMPLE" );
						
						int ndims = H5.H5Sget_simple_extent_ndims(dataspace_id);
						long[] dims = new long[ndims];
						long[] maxdims = new long[ndims];
						H5.H5Sget_simple_extent_dims(dataspace_id, dims, maxdims);
						long npoints = H5.H5Sget_simple_extent_npoints(dataspace_id);
						
						String dimstr = "";
						for ( long d : dims ) dimstr += Long.toString(d) + " ";
						info.put( "Dataspace_Dims", dimstr );
						dimstr = "";
						for ( long d : maxdims ) dimstr += Long.toString(d) + " ";
						info.put( "Dataspace_MaxDims", dimstr );
						
						info.put( "Dataspace_Number_Points", Long.toString(npoints) );
					break;
					
					case 2:
						info.put( "Dataspace_Class", "H5S_NULL" );
					break;
					
					default:
					break;
				}
					
				datatype_id = H5.H5Dget_type(dataset_id);
				info.put( "Datatype_ID", Integer.toString(datatype_id) );
					
				int datatype_size = H5.H5Tget_size(datatype_id);
				info.put( "Datatype_Size", Integer.toString(datatype_size) );
					
				int datatype_class = H5.H5Tget_class(datatype_id);
				switch (datatype_class) {
					case 0:
						info.put( "Datatype_Class", "H5T_INTEGER" );
					break;
					
					case 1:
						info.put( "Datatype_Class", "H5T_FLOAT" );
					break;
					
					case 2:
						info.put( "Datatype_Class", "H5T_TIME" );
					break;
					
					case 3:
						info.put( "Datatype_Class", "H5T_STRING" );
					break;
					
					case 4:
						info.put( "Datatype_Class", "H5T_BITFIELD" );
					break;
					
					case 5:
						info.put( "Datatype_Class", "H5T_OPAQUE" );
					break;
					
					case 6:
						info.put( "Datatype_Class", "H5T_COMPOUND" );

						int num_members = H5.H5Tget_nmembers(datatype_id);
						info.put( "Number of Members", Integer.toString(num_members));
	
						String[] member_name = new String[num_members];
						int[] member_type_id = new int[num_members];
						int[] member_type_class = new int[num_members];
						long[] member_offset = new long[num_members+1];
						member_offset[num_members] = datatype_size;
						long[] member_type_size = new long[num_members];
					
						for ( int idx=0; idx<num_members; idx++ ) {
							member_name[idx] = H5.H5Tget_member_name(datatype_id, idx);
							member_offset[idx] = H5.H5Tget_member_offset(datatype_id, idx);
							member_type_id[idx] = H5.H5Tget_member_type(datatype_id, idx);
							member_type_class[idx] = H5.H5Tget_member_class(datatype_id, idx);
						}
						for ( int idx=0; idx<num_members; idx++ ) {
							member_type_size[idx] = member_offset[idx+1] - member_offset[idx];
						}
						for ( int idx=0; idx<num_members; idx++ ) {
							info.put( "Member Index", Integer.toString(idx) );	
							info.put( "Member Name", member_name[idx] );
							info.put( "Member Offset", Long.toString(member_offset[idx]) );
							info.put( "Member Datatype ID", Integer.toString(member_type_id[idx]) );
							info.put( "Member Datatype Class", Integer.toString(member_type_class[idx]) );
							info.put( "Member Datatype Size", Long.toString(member_type_size[idx]) );
						}
						for ( int idx=0; idx<num_members; idx++ ) {
							try {
								if ( member_type_id[idx]>=0 ) H5.H5Tclose(member_type_id[idx]);
							} catch (Exception e) { e.printStackTrace(); }
						}
					break;
					
					case 7:
						info.put( "Datatype_Class", "H5T_REFERENCE" );
					break;
					
					case 8:
						info.put( "Datatype_Class", "H5T_ENUM" );
					break;
					
					case 9:
						info.put( "Datatype_Class", "H5T_VLEN" );
					break;
					
					case 10:
						info.put( "Datatype_Class", "H5T_ARRAY" );
					break;
					
					default:
					break;
				}
			} else {
				System.out.println( "\"" + linkName +  "\" is not an HDF5 data set." );
				System.exit( 1 );
			}
		} else {
			System.out.println( "\"" + linkName +  "\" doesn't exist in file \"" + fileName + "\"." );
		}
		
		try {
			if (file_id >= 0) H5.H5Fclose(file_id);
			if (datatype_id >= 0) H5.H5Tclose(datatype_id);
			if (dataspace_id >= 0) H5.H5Sclose(dataspace_id);
			if (dataset_id >= 0) H5.H5Dclose(dataset_id);
		} catch (Exception e) { e.printStackTrace(); }
		
		return info;
	}
	
	public static LinkedHashMap<String, String> getDatagroupInfo ( String fileName, String linkName ) throws Exception {
		
		int file_id = -1;
		
		try {
            file_id = H5.H5Fopen(fileName,
								 HDF5Constants.H5F_ACC_RDONLY,
								 HDF5Constants.H5P_DEFAULT);
		} catch (Exception e) { e.printStackTrace();  System.exit(1); }
		
		H5O_info_t objectInfo = null;
		int datagroup_id = -1;
		LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
		H5G_info_t groupInfo = null;
		
		if ( H5.H5Lexists(file_id, linkName, HDF5Constants.H5P_DEFAULT) ) {
			
			try {
				objectInfo = H5.H5Oget_info_by_name(file_id,
													linkName,
													HDF5Constants.H5P_DEFAULT);
			} catch (Exception e) { e.printStackTrace(); }
				
			if( objectInfo.type == 0 ) {
				try {
					datagroup_id = H5.H5Gopen(file_id,
											  linkName,
											  HDF5Constants.H5P_DEFAULT);
				} catch (Exception e) { e.printStackTrace(); }
				
				try {
					groupInfo = H5.H5Gget_info(datagroup_id);
				} catch (Exception e) { e.printStackTrace(); }
				
				info.put( "File", fileName);
				info.put( "Name", linkName);
				info.put( "Type", "Datagroup");
				info.put( "ID", Integer.toString(datagroup_id) );
				info.put( "Number_Links", Long.toString(groupInfo.nlinks) );
				switch (groupInfo.storage_type) {
					case -1: info.put( "Link_Storage_Type", "Unknown" ); break;
					case  0: info.put( "Link_Storage_Type", "Symbol_Table" ); break;
					case  1: info.put( "Link_Storage_Type", "Compact" ); break;
					case  2: info.put( "Link_Storage_Type", "Dense" ); break;
					default: break;
				}
				info.put( "Max_Corder", Long.toString(groupInfo.max_corder) );
				info.put( "Mounted", Boolean.toString(groupInfo.mounted) );
			} else {
				System.out.println( "\"" + linkName +  "\" is not an HDF5 data group." );
				System.exit( 1 );
			}
		} else {
			System.out.println( "\"" + linkName +  "\" doesn't exist in file \"" + fileName + "\"." );
		}
		
		try {
			if (file_id >= 0) H5.H5Fclose(file_id);
			if (datagroup_id >= 0) H5.H5Gclose(datagroup_id);
		} catch (Exception e) { e.printStackTrace(); }
		
		return info;
	}
	
	public static ArrayList<LinkedHashMap<String, String>> getAttribute ( String fileName, String linkName ) throws Exception {
		
		int file_id = -1;
		
		try {
            file_id = H5.H5Fopen(fileName,
								 HDF5Constants.H5F_ACC_RDONLY,
								 HDF5Constants.H5P_DEFAULT);
		} catch (Exception e) { e.printStackTrace();  System.exit(1); }
		
		H5O_info_t objectInfo = null;
		int object_id = -1;
		ArrayList<LinkedHashMap<String, String>> info = new ArrayList<LinkedHashMap<String, String>>();
		LinkedHashMap<String, String> entry = null;
		
		if ( H5.H5Lexists(file_id, linkName, HDF5Constants.H5P_DEFAULT) ) {
			
			try {
				objectInfo = H5.H5Oget_info_by_name(file_id,
													linkName,
													HDF5Constants.H5P_DEFAULT);
			} catch (Exception e) { e.printStackTrace(); }
			
			entry = new LinkedHashMap<String, String>();
			entry.put( "File", fileName );
			info.add( entry );
			
			entry = new LinkedHashMap<String, String>();
			entry.put( "Name", linkName );
			info.add( entry );
					
			if( objectInfo.type == 0 ) {

				try {
					object_id = H5.H5Gopen(file_id,
										   linkName,
										   HDF5Constants.H5P_DEFAULT);
				} catch (Exception e) { e.printStackTrace(); }
				
				entry = new LinkedHashMap<String, String>();
				entry.put( "Type", "Datagroup");
				info.add( entry );
				
				readAttributesFromObject( object_id, info );
				
			} else if( objectInfo.type == 1 ) {
				try {
					object_id = H5.H5Dopen(file_id,
										   linkName,
										   HDF5Constants.H5P_DEFAULT);
				} catch (Exception e) { e.printStackTrace(); }
				
				entry = new LinkedHashMap<String, String>();
				entry.put( "Type", "Dataset");
				info.add( entry );
				
				readAttributesFromObject( object_id, info );
				
			} else {
				System.out.println( "\"" + linkName +  "\" is not an HDF5 data group." );
				System.exit( 1 );
			}
		} else {
			System.out.println( "\"" + linkName +  "\" doesn't exist in file \"" + fileName + "\"." );
		}
		
		try {
			if (file_id >= 0) H5.H5Fclose(file_id);
			if (object_id >= 0) {
				if( objectInfo.type == 0 ) { H5.H5Gclose(object_id); }
				else if ( objectInfo.type == 1 ) { H5.H5Dclose(object_id); }
			}
		} catch (Exception e) { e.printStackTrace(); }
		
		return info;
	}
	
	public static void readAttributesFromObject(int object_id, ArrayList<LinkedHashMap<String, String>> info) throws Exception {
		
		LinkedHashMap<String, String> entry = null;
		
		entry = new LinkedHashMap<String, String>();
		entry.put( "ID", Integer.toString(object_id) );
		info.add( entry );
		
		int number_attributes = H5.H5Aget_num_attrs(object_id);
		
		entry = new LinkedHashMap<String, String>();
		entry.put( "Number_Attributes", Integer.toString(number_attributes) );
		info.add( entry );

		int attribute_id = -1;
		int attribute_space_id = -1;
		int attribute_type_id = -1; 
		String attribute_name = "";
		String attribute_value = "";
				
		for ( int idx=0; idx<number_attributes; idx++ ) {
			try {
				attribute_id = H5.H5Aopen_by_idx(object_id,
												 ".",
												 HDF5Constants.H5_INDEX_NAME, // H5_INDEX_NAME; H5_INDEX_CRT_ORDER
												 HDF5Constants.H5_ITER_INC,   // H5_ITER_INC; H5_ITER_DEC; H5_ITER_NATIVE
												 (long)idx,
												 HDF5Constants.H5P_DEFAULT,
												 HDF5Constants.H5P_DEFAULT);
			} catch (Exception e) { e.printStackTrace(); }
			
			try {
				attribute_space_id = H5.H5Aget_space(attribute_id);
			} catch (Exception e) { e.printStackTrace(); }
			
			
			long dims[] = null;
			int rank = H5.H5Sget_simple_extent_ndims(attribute_space_id);
			long lsize = 1;
			if (rank > 0) {
				dims = new long[rank];
				H5.H5Sget_simple_extent_dims(attribute_space_id, dims, null);
				for (int j = 0; j < dims.length; j++) {
					lsize *= dims[j];
				}
			}
			
			int tmptid = -1;
			try {
				tmptid = H5.H5Aget_type(attribute_id);
				attribute_type_id = H5.H5Tget_native_type(tmptid);
			} finally {
				try {
					H5.H5Tclose(tmptid);
				} catch (Exception ex) { }
			}
			
			String[] temp = { "" };
			H5.H5Aget_name(attribute_id, temp);
			attribute_name = temp[0];
			if ( H5.H5Tis_variable_str(attribute_type_id) ) {
				String[] attrValue = new String[(int) lsize];
				for (int j = 0; j < lsize; j++) { attrValue[j] = ""; }
				H5.H5AreadVL(attribute_id, attribute_type_id, attrValue);
				attribute_value = attrValue[0];
			} else {
				byte[] attrValue = new byte[H5.H5Tget_size(attribute_type_id)];
				H5.H5Aread(attribute_id, attribute_type_id, attrValue);
				attribute_value = new String(attrValue);
			}
			
			entry = new LinkedHashMap<String, String>();		
			entry.put( "Index", Integer.toString(idx) );
			entry.put( "Name", attribute_name );
			entry.put( "Value", attribute_value );
			entry.put( "Space ID", Integer.toString(attribute_space_id) );
			entry.put( "Space Class", Integer.toString(H5.H5Sget_simple_extent_type(attribute_space_id)) );
			entry.put( "Type ID", Integer.toString(attribute_type_id) );
			entry.put( "Type Class", Integer.toString(H5.H5Tget_class(attribute_type_id)) );
			//entry.put( "Character Set", Integer.toString(H5.H5Tget_cset(attribute_type_id)) );
			entry.put( "is VLen", Boolean.toString(H5.H5Tis_variable_str(attribute_type_id)) );
			entry.put( "Type Size", Integer.toString(H5.H5Tget_size(attribute_type_id)) );
			entry.put( "Storage Size", Long.toString(H5.H5Aget_storage_size(attribute_id)) );
					
			info.add( entry );
					
			try {
				if ( attribute_id>=0 ) H5.H5Aclose(attribute_id);
				if ( attribute_type_id>=0 ) H5.H5Tclose(attribute_type_id);
				if ( attribute_space_id>=0 ) H5.H5Sclose(attribute_space_id);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}

	public static void main(String args[]) throws Exception {
		
		String fileName = FILENAME[1];
		String linkName = DATASETNAME[3];
		int file_id = -1;
		int dataset_id = -1;
		int dataspace_id = -1;
		int datatype_id = -1;
		
		LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
		info = getLinkInfo( fileName, linkName );
		info.forEach( (k,v) -> System.out.println( k + ": " + v ) );
		System.out.println( );
		
		info = getObjectInfo( fileName, linkName );
		info.forEach( (k,v) -> System.out.println( k + ": " + v ) );
		System.out.println( );
		
		ArrayList<LinkedHashMap<String, String>> attributes = new ArrayList<LinkedHashMap<String, String>>();
		
		try {
            file_id = H5.H5Fopen(fileName,
								 HDF5Constants.H5F_ACC_RDONLY,
								 HDF5Constants.H5P_DEFAULT);
		} catch (Exception e) { e.printStackTrace(); }
		
		if ( H5.H5Lexists(file_id, linkName, HDF5Constants.H5P_DEFAULT) ) {
			
			H5O_info_t objectInfo = H5.H5Oget_info_by_name(file_id, linkName, HDF5Constants.H5P_DEFAULT);

			switch (objectInfo.type) {
				
				case 0: // Group
					info = getDatagroupInfo( fileName, linkName );
					info.forEach( (k,v) -> System.out.println( k + ": " + v ) );
					System.out.println( );
					
					attributes = getAttribute( fileName, linkName );
					attributes.forEach( attribute -> {
						attribute.forEach( (k,v) -> System.out.println( k + ": " + v ) );
					});
					System.out.println( );
				break;
				
				case 1: // Dataset
					
					info = getDatasetInfo( fileName, linkName );
					info.forEach( (k,v) -> System.out.println( k + ": " + v ) );
					System.out.println( );
					
					attributes = getAttribute( fileName, linkName );
					attributes.forEach( attribute -> {
						attribute.forEach( (k,v) -> System.out.println( k + ": " + v ) );
					});
					System.out.println( );



					dataset_id = H5.H5Dopen(file_id, linkName, HDF5Constants.H5P_DEFAULT);
					dataspace_id = H5.H5Dget_space(dataset_id);
					int dataspace_class = H5.H5Sget_simple_extent_type(dataspace_id);

					int ndims = -1;
					long npoints = -1;
					long[] dims = null;
					long[] maxdims = null;
					switch (dataspace_class) {
						
						case 0: // H5S_SCALAR
						break;
						
						case 1: // H5S_SIMPLE
							ndims = H5.H5Sget_simple_extent_ndims(dataspace_id);
							dims = new long[ndims];
							maxdims = new long[ndims];
							H5.H5Sget_simple_extent_dims(dataspace_id, dims, maxdims);
							npoints = H5.H5Sget_simple_extent_npoints(dataspace_id);
						break;
						
						case 2: // H5S_NULL
						break;
						
						default:
						break;
					}
					
					datatype_id = H5.H5Dget_type(dataset_id);
					int datatype_size = H5.H5Tget_size(datatype_id);
					int datatype_class = H5.H5Tget_class(datatype_id);
					switch (datatype_class) {
						
						case 0: // H5T_INTEGER
							switch (ndims) {
								case 1:
									int[] int1D = new int[(int)dims[0]];
									H5.H5Dread(dataset_id, datatype_id, dataspace_id, dataspace_id, HDF5Constants.H5P_DEFAULT, int1D);
								break;
								
								case 2:
									int[][] int2D = new int[(int)dims[0]][(int)dims[1]];
									H5.H5Dread(dataset_id, datatype_id, dataspace_id, dataspace_id, HDF5Constants.H5P_DEFAULT, int2D);
								break;
								
								default:
								break;
							}
						break;
						
						case 1: // H5T_FLOAT
							if ( datatype_size==4 ) {
								switch (ndims) {
									case 1:
										float[] float1D = new float[(int)dims[0]];
										H5.H5Dread(dataset_id, datatype_id, dataspace_id, dataspace_id, HDF5Constants.H5P_DEFAULT, float1D);
									break;
									
									case 2:
										float[][] float2D = new float[(int)dims[0]][(int)dims[1]];
										H5.H5Dread(dataset_id, datatype_id, dataspace_id, dataspace_id, HDF5Constants.H5P_DEFAULT, float2D);
									break;
									
									default:
									break;
								}
							} else if ( datatype_size==8 ) {
								switch (ndims) {
									case 1:
										double[] double1D = new double[(int)dims[0]];
										H5.H5Dread(dataset_id, datatype_id, dataspace_id, dataspace_id, HDF5Constants.H5P_DEFAULT, double1D);
									break;
									
									case 2:
										double[][] double2D = new double[(int)dims[0]][(int)dims[1]];
										H5.H5Dread(dataset_id, datatype_id, dataspace_id, dataspace_id, HDF5Constants.H5P_DEFAULT, double2D);
									break;
									
									default:
									break;
								}
							}
						break;
						
						case 2: // H5T_TIME
						break;
						
						case 3: // H5T_STRING
						break;
						
						case 4: // H5T_BITFIELD
						break;
						
						case 5: // H5T_OPAQUE
						break;
							
						case 6: // H5T_COMPOUND
						
							int num_members = H5.H5Tget_nmembers(datatype_id);
	
							int[] member_idx = new int[num_members];
							for ( int i=0; i<num_members; i++ ) member_idx[i] = i; 
							String[] member_name = new String[num_members];
							int[] member_type_id = new int[num_members];
							int[] member_type_class = new int[num_members];
							long[] member_offset = new long[num_members+1];
							member_offset[num_members] = datatype_size;
							long[] member_type_size = new long[num_members];
					
							for ( int idx : member_idx ) {
								member_name[idx] = H5.H5Tget_member_name(datatype_id, idx);
								member_offset[idx] = H5.H5Tget_member_offset(datatype_id, idx);
								member_type_id[idx] = H5.H5Tget_member_type(datatype_id, idx);
								member_type_class[idx] = H5.H5Tget_member_class(datatype_id, idx);
							}
							for ( int idx : member_idx ) {
								member_type_size[idx] = member_offset[idx+1] - member_offset[idx];
							}
							
							int tid = H5.H5Tcreate(HDF5Constants.H5T_COMPOUND, member_type_size[0]);
							H5.H5Tinsert(tid, member_name[0], 0, H5.H5Tget_native_type(member_type_id[0]));
							
							switch (member_type_class[0]) {
								case 0: // H5T_INTEGER
									switch (ndims) {
										case 1:
											int[] int1D = new int[(int)dims[0]];
											H5.H5Dread(dataset_id, tid, dataspace_id, dataspace_id, HDF5Constants.H5P_DEFAULT, int1D);
											System.out.println(member_name[0] + "[" + 0 + "]: " + int1D[0]);
											System.out.println(member_name[0] + "[" + 5 + "]: " + int1D[5]);
										break;

										case 2:
											int[][] int2D = new int[(int)dims[0]][(int)dims[1]];
											H5.H5Dread(dataset_id, tid, dataspace_id, dataspace_id, HDF5Constants.H5P_DEFAULT, int2D);
											System.out.println(member_name[0] + "[" + 0 + "][" + 0 + "]: " + int2D[0][0]);
											System.out.println(member_name[0] + "[" + 5 + "][" + 0 + "]: " + int2D[5][0]);
										break;
										
										default:
										break;
									}
								break;
								
								case 1: // H5T_FLOAT
									if ( member_type_size[0]==4 ) { //System.out.println("OK");
										switch (ndims) {
											case 1:
												float[] float1D = new float[(int)dims[0]];
												H5.H5Dread(dataset_id, tid, dataspace_id, dataspace_id, HDF5Constants.H5P_DEFAULT, float1D);
												System.out.println(member_name[0] + "[" + 0 + "]: " + float1D[0]);
												System.out.println(member_name[0] + "[" + 5 + "]: " + float1D[5]);
											break;
											
											case 2:
												float[][] float2D = new float[(int)dims[0]][(int)dims[1]];
												H5.H5Dread(dataset_id, tid, dataspace_id, dataspace_id, HDF5Constants.H5P_DEFAULT, float2D);
												System.out.println(member_name[0] + "[" + 0 + "][" + 0 + "]: " + float2D[0][0]);
												System.out.println(member_name[0] + "[" + 5 + "][" + 0 + "]: " + float2D[5][0]);
											break;
											
											default:
											break;
										}
									} else if ( member_type_size[0]==8 ) {
										switch (ndims) {
											case 1:
												double[] double1D = new double[(int)dims[0]];
												H5.H5Dread(dataset_id, tid, dataspace_id, dataspace_id, HDF5Constants.H5P_DEFAULT, double1D);
												System.out.println(member_name[0] + "[" + 0 + "]: " + double1D[0]);
												System.out.println(member_name[0] + "[" + 5 + "]: " + double1D[5]);
											break;
											
											case 2:
												double[][] double2D = new double[(int)dims[0]][(int)dims[1]];
												H5.H5Dread(dataset_id, tid, dataspace_id, dataspace_id, HDF5Constants.H5P_DEFAULT, double2D);
												System.out.println(member_name[0] + "[" + 0 + "][" + 0 + "]: " + double2D[0][0]);
												System.out.println(member_name[0] + "[" + 5 + "][" + 0 + "]: " + double2D[5][0]);
											break;
											
											default:
											break;
										}
									}
								break;
								
								default:
								break;
							}
						
							try {
								if ( tid>=0 ) H5.H5Tclose(tid);
							} catch (Exception e) { e.printStackTrace(); }
							for ( int idx : member_idx ) {
								try {
									if ( member_type_id[idx]>=0 ) H5.H5Tclose(member_type_id[idx]);
								} catch (Exception e) { e.printStackTrace(); }
							}
							
						break;
						
						case 7: // H5T_REFERENCE
						break;
						
						case 8: // H5T_ENUM
						break;
						
						case 9: // H5T_VLEN
						break;
						
						case 10: // H5T_ARRAY
						break;
							
						default:
						break;
					}
				break;
				
				case 2: // Committed (Named) Datatype
				break;
				
				default:
				break;
				}
			} else {
				System.out.println(linkName+" doesn't exist.\n");
			}
		
			try {
				if (datatype_id >= 0) H5.H5Tclose(datatype_id);
				if (dataspace_id >= 0) H5.H5Sclose(dataspace_id);
				if (dataset_id >= 0) H5.H5Dclose(dataset_id);
				if (file_id >= 0) H5.H5Fclose(file_id);
			} catch (Exception e) { e.printStackTrace(); }
    
		}

}
