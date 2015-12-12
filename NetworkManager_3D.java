import java.util.*;



public class NetworkManager_3D {
	
	private static NetworkManager_3D netManager = null;

	
	private static Network_3D network = null;

	private static StatisticalData_3D statData = null;

	
	private static HelpingUtility_3D helpUtility = null;

	
	private static int curSet;

	
	private static String paramFile;

	
	private static double warm_up_percentage = 0.1;


	public NetworkManager_3D(String parameterFile) {
		NetworkManager_3D.paramFile = parameterFile;

		helpUtility = new HelpingUtility_3D();
		helpUtility.readParameterFromFile(NetworkManager_3D.paramFile);
		curSet = 0;
	}


	public boolean createNextNetwork() {
		helpUtility.setRandomSeed();
		Vector paramSet = helpUtility.getParamSet(curSet);
		if (null != paramSet) {
			curSet++;
			loadSet(paramSet);
			network = new Network_3D(IConstants_3D.CURRENT_NET);
			statData = new StatisticalData_3D(IConstants_3D.CURRENT_NET);
			return true;
		}
		return false;
	}

	
	public void initializeNetwork() {
		if (null != network) {
			network = new Network_3D(IConstants_3D.CURRENT_NET);
			statData = new StatisticalData_3D(IConstants_3D.CURRENT_NET);
		}
	}

	
	private void loadSet(Vector set) {
		int i;
		ParamDTO_3D parDTO;
		String param, val;

		if (null != set) {
			for (i = 0; i < set.size(); i++) {
				parDTO = (ParamDTO_3D) set.get(i);
				param = parDTO.getParam();
				val = parDTO.getVal();
				if (param.equalsIgnoreCase("CURRENT_NET"))
					IConstants_3D.CURRENT_NET = Integer.parseInt(val);
				else if (param.equalsIgnoreCase("AVG_INTER_ARRIVAL"))
					IConstants_3D.AVG_INTER_ARRIVAL = Integer.parseInt(val);
				else if (param.equalsIgnoreCase("AVG_MESSAGE_LENGTH"))
					IConstants_3D.AVG_MESSAGE_LENGTH = Integer.parseInt(val);
				else if (param.equalsIgnoreCase("FLIT_LENGTH"))
					IConstants_3D.FLIT_LENGTH = Integer.parseInt(val);
				else if (param.equalsIgnoreCase("NUMBER_OF_IP_NODE"))
					IConstants_3D.NUMBER_OF_IP_NODE = Integer.parseInt(val);
				else if (param.equalsIgnoreCase("CURRENT_ADJ_SWITCH"))
					IConstants_3D.CURRENT_ADJ_SWITCH = Integer.parseInt(val);
				else if (param.equalsIgnoreCase("CURRENT_LINK_COUNT"))
					IConstants_3D.CURRENT_LINK_COUNT = Integer.parseInt(val);
				else if (param.equalsIgnoreCase("CURRENT_VC_COUNT"))
					IConstants_3D.CURRENT_VC_COUNT = Integer.parseInt(val);
				else if (param.equalsIgnoreCase("NUM_FLIT_PER_BUFFER"))
					IConstants_3D.NUM_FLIT_PER_BUFFER = Integer.parseInt(val);
				else if (param.equalsIgnoreCase("NUM_CYCLE"))
					IConstants_3D.NUM_CYCLE = Integer.parseInt(val);
				else if (param.equalsIgnoreCase("NUM_RUN"))
					IConstants_3D.NUM_RUN = Integer.parseInt(val);
				else if (param.equalsIgnoreCase("TRACE"))
					IConstants_3D.TRACE = Boolean.valueOf(val).booleanValue();
				else if (param.equalsIgnoreCase("ASYNCHRONOUS"))
					IConstants_3D.ASYNCHRONOUS = Boolean.valueOf(val)
							.booleanValue();
				else if (param.equalsIgnoreCase("TRAFFIC_TYPE"))
					IConstants_3D.TRAFFIC_TYPE = Integer.parseInt(val);
				else if (param.equalsIgnoreCase("WARM_UP_CYCLE"))
					warm_up_percentage = Double.parseDouble(val);
				else if (param.equalsIgnoreCase("FIXED_MESSAGE_LENGTH"))
					IConstants_3D.FIXED_MESSAGE_LENGTH = Boolean.valueOf(val)
							.booleanValue();
				
			}
		}

		// default value of warm_up_percentage= 0.1
		IConstants_3D.WARM_UP_CYCLE = (int) (IConstants_3D.NUM_CYCLE * warm_up_percentage);
	}

	public static NetworkManager_3D getInstance(String paramFile) {
		if (netManager == null) {
			netManager = new NetworkManager_3D(paramFile);
		}
		return netManager;
	}

	
	public static NetworkManager_3D getInstance() {
		if (netManager == null) {
			netManager = new NetworkManager_3D(IConstants_3D.PARAM_FILE);
		}
		return netManager;
	}


	public static Network_3D getNetworkInstance() {
		return network;
	}

	
	public static HelpingUtility_3D getHelpingUtility() {
		return helpUtility;
	}


	public static StatisticalData_3D getStatDataInstance() {
		if (netManager == null) {
			netManager = new NetworkManager_3D(paramFile);
		}
		return statData;
	}

}