

public class StatisticalData_3D {
	
	private int noOfSwitch;

	
	private long flitLeavingFromSwitch[];

	
	private long packetDelay[];

	
	private long packetProduced[];

	
	private long packetReceived[];

	
	private long messageNotProduced[];

	private long packetSent[];

	
	private long flitReceived[];

	
	private long switchLinkUseStatus[];

	
	private long numSwitchLink[];

	
	private long nodeLinkUseStatus[];

	
	private long switchInBufferUseStatus[];

	
	private long switchOutBufferUseStatus[];

	
	private long nodeInBufferUseStatus[];

	private long nodeOutBufferUseStatus[];

	
	private long[] packetHopCount;

	
	public int sameUnit = 0;

	
	public StatisticalData_3D(int netType) {
		int i;
		messageNotProduced = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		packetDelay = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		packetProduced = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		packetReceived = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		packetSent = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		nodeLinkUseStatus = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		nodeInBufferUseStatus = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		nodeOutBufferUseStatus = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		flitReceived = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		packetHopCount = new long[IConstants_3D.NUMBER_OF_IP_NODE];

		for (i = 0; i < IConstants_3D.NUMBER_OF_IP_NODE; i++) {
			messageNotProduced[i] = 0;
			packetDelay[i] = 0;
			packetProduced[i] = 0;
			packetReceived[i] = 0;
			packetSent[i] = 0;
			nodeLinkUseStatus[i] = 0;
			nodeInBufferUseStatus[i] = 0;
			nodeOutBufferUseStatus[i] = 0;
			flitReceived[i] = 0;
			packetHopCount[i] = 0;
		}

		
		noOfSwitch = IConstants_3D.NUMBER_OF_SWITCH;

		flitLeavingFromSwitch = new long[noOfSwitch];
		switchLinkUseStatus = new long[noOfSwitch];
		numSwitchLink = new long[noOfSwitch];
		switchInBufferUseStatus = new long[noOfSwitch];
		switchOutBufferUseStatus = new long[noOfSwitch];
		for (i = 0; i < noOfSwitch; i++) {
			flitLeavingFromSwitch[i] = 0;
			switchLinkUseStatus[i] = 0;
			numSwitchLink[i] = 0;
			switchInBufferUseStatus[i] = 0;
			switchOutBufferUseStatus[i] = 0;
		}

	}

	
	public void initializeStat() {
		int i;
		messageNotProduced = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		packetDelay = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		packetProduced = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		packetReceived = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		packetSent = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		nodeLinkUseStatus = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		nodeInBufferUseStatus = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		nodeOutBufferUseStatus = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		flitReceived = new long[IConstants_3D.NUMBER_OF_IP_NODE];
		packetHopCount = new long[IConstants_3D.NUMBER_OF_IP_NODE];

		for (i = 0; i < IConstants_3D.NUMBER_OF_IP_NODE; i++) {
			messageNotProduced[i] = 0;
			packetDelay[i] = 0;
			packetProduced[i] = 0;
			packetReceived[i] = 0;
			packetSent[i] = 0;
			nodeLinkUseStatus[i] = 0;
			nodeInBufferUseStatus[i] = 0;
			nodeOutBufferUseStatus[i] = 0;
			flitReceived[i] = 0;
			packetHopCount[i] = 0;
		}

	
		noOfSwitch = IConstants_3D.NUMBER_OF_SWITCH;

		flitLeavingFromSwitch = new long[noOfSwitch];
		switchLinkUseStatus = new long[noOfSwitch];
		numSwitchLink = new long[noOfSwitch];
		switchInBufferUseStatus = new long[noOfSwitch];
		switchOutBufferUseStatus = new long[noOfSwitch];

		for (i = 0; i < noOfSwitch; i++) {
			flitLeavingFromSwitch[i] = 0;
			switchLinkUseStatus[i] = 0;
			numSwitchLink[i] = 0;
			switchInBufferUseStatus[i] = 0;
			switchOutBufferUseStatus[i] = 0;
		}
	}

	
	public void incrementFlitReceived(int nodeIndex) {
		flitReceived[nodeIndex]++;
	}

	
	public void incrementMessageNotProduced(int nodeIndex) {
		messageNotProduced[nodeIndex]++;
	}

	
	public void incrementFlitLeavingFromSwitch(int switchIndex) {
		flitLeavingFromSwitch[switchIndex]++;
	}

	
	public void incrementPacketHopCount(int nodeIndex, int hop) {
		packetHopCount[nodeIndex] += hop;
	}

	
	public void incrementPacketDelay(int nodeIndex, int delay) {
		packetDelay[nodeIndex] += delay;
		packetReceived[nodeIndex]++;
	}

	
	public void incrementPacketSent(int nodeIndex) {
		packetSent[nodeIndex]++;
	}

	
	public void incrementPacketProduced(int nodeIndex) {
		packetProduced[nodeIndex]++;
	}

	

	public void incrementSwitchLinkUse(int switchIndex, int useCount) {
		switchLinkUseStatus[switchIndex] += useCount;
	}


	public void setSwitchNumLink(int switchIndex, int count) {
		numSwitchLink[switchIndex] = count ;
	}

	public void incrementNodeLinkUse(int nodeIndex) {
		nodeLinkUseStatus[nodeIndex]++;
	}

	public void incrementSwitchInputBufferUse(int switchIndex, int useCount) {
		switchInBufferUseStatus[switchIndex] += useCount;
	}

	
	public void incrementSwitchOutputBufferUse(int switchIndex, int useCount) {
		switchOutBufferUseStatus[switchIndex] += useCount;
	}


	public void incrementNodeInputBufferUse(int nodeIndex, int useCount) {
		nodeInBufferUseStatus[nodeIndex] += useCount;
	}

	
	public void incrementNodeOutputBufferUse(int nodeIndex, int useCount) {
		nodeOutBufferUseStatus[nodeIndex] += useCount;
	}

	
	public double getThroughput(int numCycle) {
		long temp = 0;
		for (int i = 0; i < noOfSwitch; i++) {
			temp += flitLeavingFromSwitch[i];
		}
		System.out.println("Num Switch = " + noOfSwitch);
		return (double) temp / ((double) noOfSwitch * numCycle);
	}

	
	public double getAvgPacketDelay() {
		double temp1 = 0;
		long numSent = 0, numRecv = 0;
		for (int i = 0; i < IConstants_3D.NUMBER_OF_IP_NODE; i++) {
			temp1 += packetDelay[i];
			numSent += packetSent[i];
			numRecv += packetReceived[i];
			

		}
		System.out.println("Number of Packets Sent =���� " + numSent);
		System.out.println("Number of Packets Received = " + numRecv);

		
		return (double) temp1 / numRecv;
	}

	
	public double getAvgPacketHopCount() {
		long temp1 = 0;
		long numRecv = 0;
		for (int i = 0; i < IConstants_3D.NUMBER_OF_IP_NODE; i++) {
			temp1 += packetHopCount[i];
			numRecv += packetReceived[i];
		}
		return (double) temp1 / numRecv;

	}

	
	public double getNumberOfPacketSent() {
		double numSent = 0;
		for (int i = 0; i < IConstants_3D.NUMBER_OF_IP_NODE; i++) {
			numSent += packetSent[i];

		}

		return numSent;
	}

	
	public double getNumberOfPacketReceived() {
		double numReceived = 0;
		for (int i = 0; i < IConstants_3D.NUMBER_OF_IP_NODE; i++) {
			numReceived += packetReceived[i];

		}

		return numReceived;
	}

	
	public double getNumberOfPacketProduced() {
		double numProd = 0;
		for (int i = 0; i < IConstants_3D.NUMBER_OF_IP_NODE; i++) {
			numProd += packetProduced[i];
			System.out.println("Node: " + i + " .. produces"
					+ packetProduced[i]);
		}
		return numProd;
	}

	
	public double getAvgMessageNotProduced() {
		double temp1 = 0;

		for (int i = 0; i < IConstants_3D.NUMBER_OF_IP_NODE; i++) {
			temp1 += messageNotProduced[i];
			System.out.println("Node: " + i + " .. not  produces"
					+ messageNotProduced[i]);
		}
		return temp1 / IConstants_3D.NUMBER_OF_IP_NODE;
	}

	
	public double getLinkUtilization() {
		double temp1 = 0;
		float numLink = 0;
		for (int i = 0; i < noOfSwitch; i++) {
			temp1 += switchLinkUseStatus[i];
			numLink += numSwitchLink[i];
			System.out.println("Switch: " + i + " .. link Utilization: "
					+ (double) switchLinkUseStatus[i] / IConstants_3D.NUM_CYCLE);
		}

		for (int i = 0; i < IConstants_3D.NUMBER_OF_IP_NODE; i++) {
			temp1 += nodeLinkUseStatus[i];
			
		}
		numLink += IConstants_3D.NUMBER_OF_IP_NODE;

		System.out.println("Net =" + IConstants_3D.CURRENT_NET + " Active Link "
				+ numLink);

		return temp1 / (numLink * IConstants_3D.NUM_CYCLE);
	}

	
	public double getInputBufferNodeUtilization() {
		double temp1 = 0.0, temp2;

		for (int i = 0; i < IConstants_3D.NUMBER_OF_IP_NODE; i++) {
			temp1 += nodeInBufferUseStatus[i];
		}
		temp2 = ((double) IConstants_3D.CURRENT_VC_COUNT
				* (double) IConstants_3D.NUM_FLIT_PER_BUFFER
				* (double) IConstants_3D.NUMBER_OF_IP_NODE * (double) IConstants_3D.NUM_CYCLE);
		return temp1 / temp2;
	}

	
	public double getOutputBufferNodeUtilization() {
		double temp1 = 0.0, temp2;

		for (int i = 0; i < IConstants_3D.NUMBER_OF_IP_NODE; i++) {
			temp1 += nodeOutBufferUseStatus[i];
		}
		temp2 = ((double) IConstants_3D.CURRENT_VC_COUNT
				* (double) IConstants_3D.NUM_FLIT_PER_BUFFER
				* (double) IConstants_3D.NUMBER_OF_IP_NODE * (double) IConstants_3D.NUM_CYCLE);
		return temp1 / temp2;

	}

	
	public double getInputBufferSwitchUtilization() {
		double temp1 = 0.0, temp2;
		long numLink = 0;

		for (int i = 0; i < noOfSwitch; i++) {
			temp1 += switchInBufferUseStatus[i];
			numLink += numSwitchLink[i];
			System.out.println("Switch: " + i + " .. inbuf: "
					+ (double) switchInBufferUseStatus[i]
					/ IConstants_3D.NUM_CYCLE);
		}


		
		temp2 = (double) IConstants_3D.CURRENT_VC_COUNT
				* (double) IConstants_3D.NUM_FLIT_PER_BUFFER * (double) numLink
				* (double) IConstants_3D.NUM_CYCLE;

		return temp1 / temp2;
	}

	
	public double getOutputBufferSwitchUtilization() {
		
		double temp1 = 0.0, temp2;
		long numLink = 0;

		for (int i = 0; i < noOfSwitch; i++) {
			temp1 += switchOutBufferUseStatus[i];
			numLink += numSwitchLink[i];
			System.out.println("Switch: " + i + " .. outbuffer Utilization: "
					+ (double) switchOutBufferUseStatus[i]
					/ IConstants_3D.NUM_CYCLE);
		}

		

		temp2 = (double) IConstants_3D.CURRENT_VC_COUNT
				* (double) IConstants_3D.NUM_FLIT_PER_BUFFER * (double) numLink
				* (double) IConstants_3D.NUM_CYCLE;
		
		return temp1 / temp2;

	}

	
	public double getBufferUtilization() {
		double temp1 = 0.0, temp2;
		long numLinkSwitch = 0;
				
		for (int i = 0; i < IConstants_3D.NUMBER_OF_IP_NODE; i++) {
			temp1 += nodeInBufferUseStatus[i];
			temp1 += nodeOutBufferUseStatus[i];
		}
		for (int i = 0; i < noOfSwitch; i++) {
			temp1 += switchInBufferUseStatus[i];
			temp1 += switchOutBufferUseStatus[i];
			numLinkSwitch += numSwitchLink[i];
		}
		
		
		temp2 = 2 * ((double) IConstants_3D.CURRENT_VC_COUNT
				* (double) IConstants_3D.NUM_FLIT_PER_BUFFER
				* ((double) numLinkSwitch + IConstants_3D.NUMBER_OF_IP_NODE ) * (double) IConstants_3D.NUM_CYCLE);
		return temp1 / temp2;
	}

	
	public double getNetworkNodeThroughput() {
		long temp1 = 0;
		
		for (int i = 0; i < IConstants_3D.NUMBER_OF_IP_NODE; i++) {
			temp1 += flitReceived[i];
		}
		return (double) temp1
				/ (IConstants_3D.NUMBER_OF_IP_NODE * IConstants_3D.NUM_CYCLE);
	}
}