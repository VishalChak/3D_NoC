

public class IConstants_3D {
	public IConstants_3D() {
	}

	
	public static String PARAM_FILE = "nocSimParameter.txt";

	
	public static String OUT_FILE = "nocSimOutput.txt";

	
	public static String TRACE_FILE = "nocSimTrace.txt";


	public static int HEADER_FLIT = 0;

	
	public static int DATA_FLIT = 1;

	
	public static int NULL_FLIT = 2;

	public static int MESH_ROW = 10;

	
	public static int MESH_COL = 10;

	
	public static int MESH_ROW_BITS = 14;

	
	public static int MESH_COL_BITS = 14;

	
	public static int MESH_ADJ_NODE = 1;

	
	public static int MESH_NODE_BITS_REQ = 2;

	
	public static int INT_SIZE = 32;

	
	public static int SWITCH_LEFT = 0;

	
	public static int SWITCH_TOP = 1;

	
	public static int SWITCH_RIGHT = 2;

	
	public static int SWITCH_BOTTOM = 3;
	
	
	public static int SWITCH_UP = 4;
	
	public static int SWITCH_DOWN = 5;

	
	public static int NUM_ADDR_BITS = 6;

	
	public static int NUM_VCID_BITS = 4;

	public static int Z_AXIS_BITS = 0 ;
	
	public static int NUM_FLITS_BITS = 16;

	
	public static int NUM_FLIT_TYPE_BITS = 1;

	
	public static int CUR_CYCLE = 0;


	public static int MAX_MESSAGE_NUMBER = 200;

	
	public static int DEFAULT_VC_COUNT = 6;

	
	public static int TRAFFIC_TYPE = 0;

	
	public static int TRAFFIC_UNIFORM = 0;

	
	public static int TRAFFIC_LOCAL = 1;

	
	public static boolean FIXED_MESSAGE_LENGTH = false;


	
	public static int NET_MESH = 1;




	public static int WARM_UP_CYCLE = 0;

	
	public static int CURRENT_NET = 1;

	
	public static int AVG_INTER_ARRIVAL = 20;

	
	public static int AVG_MESSAGE_LENGTH = 100;// bytes

	
	public static int FLIT_LENGTH = 64; // bits

	
	public static int NUMBER_OF_IP_NODE = 100;

	
	public static int NUMBER_OF_SWITCH = 100;


	public static int CURRENT_ADJ_SWITCH = 6;

	
	public static int CURRENT_LINK_COUNT = 6;

	
	public static int CURRENT_VC_COUNT = 4;

	
	public static int NUM_FLIT_PER_BUFFER = 2;

	
	public static int NUM_CYCLE = 300;

	
	public static int NUM_RUN = 4;


	public static boolean TRACE = false;

	
	public static boolean ASYNCHRONOUS = false;


}
