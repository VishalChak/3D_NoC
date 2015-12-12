import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;


public class ConcreteNodeTraffic_3D extends NodeTraffic_3D {

	public static long LOCAL_IN = 0;

	public static long LOCAL_OUT = 0;

	
	public ConcreteNodeTraffic_3D(int address) {
		super(address);

	}


	public Vector generateMessage(int curCycle, int curMessageCount) {
		Vector packet = new Vector();
		int vcId = 0; 
		int i;

		if (curMessageCount >= IConstants_3D.MAX_MESSAGE_NUMBER) {
			nextMsgGenTime = curCycle + 1;
			return null;
		}
		int noOfFlit = this.getMessageSize();

		int destination = 0;

		destination = this.getDestination();
		packet.add(createHeaderFlit(destination, noOfFlit, vcId, curCycle));

		for (i = 1; i < noOfFlit; i++) {
			packet.add(createDataFlit(vcId, curCycle, destination));
		}
		setNextMsgGenTime(curCycle);

		return packet;
	}

	
	public void setNextMsgGenTime(int curCycle) {

		nextMsgGenTime = (int) (-1 * IConstants_3D.AVG_INTER_ARRIVAL * Math
				.log(NetworkManager_3D.getHelpingUtility().getNextRandomNumber()))
				+ curCycle + 1;

		if (IConstants_3D.TRACE) {
			try {
				RandomAccessFile raf = new RandomAccessFile(
						IConstants_3D.TRACE_FILE, "rw");
				raf.seek(raf.length());
				raf.writeBytes("\nCycle " + curCycle + " Node " + address
						+ " will produce Message at " + nextMsgGenTime);

				raf.close();
			} catch (IOException ioex) {
			}
		}

	}

	

	protected int getNextMsgGenTime() {

		return this.nextMsgGenTime;
	}

	

	protected int getDestination() {

		int destination = 0;

		if (IConstants_3D.CURRENT_NET == IConstants_3D.NET_MESH) {
			destination = generateDestinationForMeshNetwork(address);
		}

		return destination;
	}

	
	protected Flit_3D createHeaderFlit(int destination, int noOfFlit, int vcId,
			int curCycle) {

		int flitData = 0, bitUsed = 0;
		int noOfInt = 0;
		int data[];
		int minFlitLength = IConstants_3D.FLIT_LENGTH;
		IConstants_3D.NUM_VCID_BITS = (int) Math.ceil(Math
				.log(IConstants_3D.CURRENT_VC_COUNT)
				/ Math.log(2));
		if (IConstants_3D.CURRENT_NET == IConstants_3D.NET_MESH) {
			minFlitLength = IConstants_3D.NUM_FLIT_TYPE_BITS
					+ IConstants_3D.NUM_VCID_BITS + IConstants_3D.NUM_ADDR_BITS
					+ IConstants_3D.NUM_FLITS_BITS + IConstants_3D.MESH_ROW_BITS
					+ IConstants_3D.MESH_COL_BITS + IConstants_3D.MESH_NODE_BITS_REQ
					+ IConstants_3D.MESH_ROW_BITS + IConstants_3D.MESH_COL_BITS
					+ IConstants_3D.MESH_NODE_BITS_REQ;
		} 

		if (minFlitLength % IConstants_3D.INT_SIZE > 0) {
			minFlitLength += IConstants_3D.INT_SIZE
					- (minFlitLength % IConstants_3D.INT_SIZE);
		}
		if (minFlitLength < IConstants_3D.FLIT_LENGTH) {
			minFlitLength = IConstants_3D.FLIT_LENGTH;
		}

		data = new int[minFlitLength / IConstants_3D.INT_SIZE];

		flitData = IConstants_3D.HEADER_FLIT; // 1 bit for flit type
		bitUsed += IConstants_3D.NUM_FLIT_TYPE_BITS;

		flitData = (vcId << bitUsed) | flitData; 
		// bits for VCID
		bitUsed += IConstants_3D.NUM_VCID_BITS;

		int numAddrBits = 30;
		if (IConstants_3D.CURRENT_NET == IConstants_3D.NET_MESH
				) {

			numAddrBits = IConstants_3D.MESH_ROW_BITS + IConstants_3D.MESH_COL_BITS
					+ IConstants_3D.MESH_NODE_BITS_REQ;
		} 

		flitData = (numAddrBits << bitUsed) | flitData; //
		bitUsed += IConstants_3D.NUM_ADDR_BITS; // how many bits required to
		// represent number of bits in
		// address

		if (bitUsed + IConstants_3D.NUM_FLITS_BITS > IConstants_3D.INT_SIZE) {
			int rest = bitUsed + IConstants_3D.NUM_FLITS_BITS
					- IConstants_3D.INT_SIZE;
			int temp = noOfFlit
					% (int) Math.pow(2, IConstants_3D.NUM_FLITS_BITS - rest);

			flitData = (temp << bitUsed) | flitData;
			data[noOfInt++] = flitData;
			flitData = noOfFlit >>> (IConstants_3D.NUM_FLITS_BITS - rest);
			bitUsed = rest;
		} else {
			flitData = (noOfFlit << bitUsed) | flitData;
			bitUsed += IConstants_3D.NUM_FLITS_BITS;
		}

		// source address
		if (bitUsed + numAddrBits > IConstants_3D.INT_SIZE) {
			int rest = bitUsed + numAddrBits - IConstants_3D.INT_SIZE;
			int temp = address % (int) Math.pow(2, numAddrBits - rest);
			flitData = (temp << bitUsed) | flitData;
			data[noOfInt] = flitData;
			noOfInt++;
			flitData = address >>> (numAddrBits - rest);
			bitUsed = rest;
		} else {
			flitData = (address << bitUsed) | flitData;
			bitUsed += numAddrBits;
		}

		// dest address
		if (bitUsed + numAddrBits > IConstants_3D.INT_SIZE) {
			int rest = bitUsed + numAddrBits - IConstants_3D.INT_SIZE;
			int temp = destination % (int) Math.pow(2, numAddrBits - rest);
			flitData = (temp << bitUsed) | flitData;
			data[noOfInt] = flitData;
			noOfInt++;
			flitData = destination >>> (numAddrBits - rest);
			bitUsed = rest;
		} else {
			flitData = (destination << bitUsed) | flitData;
			bitUsed += numAddrBits;
		}
		// rest bits
		data[noOfInt] = flitData;

		Flit_3D header = new Flit_3D(data, curCycle);
	
		header.setDest(destination);
		header.setSource(this.address);
		
		  
		return header;
	}

	
	protected Flit_3D createDataFlit(int vcId, int curCycle, int destination) {
		int flitData = 0;
		int bitUsed = 0;
		int minFlitLength = IConstants_3D.FLIT_LENGTH;
		IConstants_3D.NUM_VCID_BITS = (int) Math.ceil(Math
				.log(IConstants_3D.CURRENT_VC_COUNT)
				/ Math.log(2));
		if (IConstants_3D.CURRENT_NET == IConstants_3D.NET_MESH
				) {
			minFlitLength = IConstants_3D.NUM_FLIT_TYPE_BITS
					+ IConstants_3D.NUM_VCID_BITS + IConstants_3D.NUM_ADDR_BITS
					+ IConstants_3D.NUM_FLITS_BITS + IConstants_3D.MESH_ROW_BITS
					+ IConstants_3D.MESH_COL_BITS + IConstants_3D.MESH_NODE_BITS_REQ
					+ IConstants_3D.MESH_ROW_BITS + IConstants_3D.MESH_COL_BITS
					+ IConstants_3D.MESH_NODE_BITS_REQ;
		} 
		if (minFlitLength % IConstants_3D.INT_SIZE > 0) {
			minFlitLength += IConstants_3D.INT_SIZE
					- (minFlitLength % IConstants_3D.INT_SIZE);
		}
		if (minFlitLength < IConstants_3D.FLIT_LENGTH) {
			minFlitLength = IConstants_3D.FLIT_LENGTH;
		}

		int noOfInt = minFlitLength / IConstants_3D.INT_SIZE;
		int data[] = new int[noOfInt];

		flitData = IConstants_3D.DATA_FLIT; // 1 bit for flit type
		bitUsed += IConstants_3D.NUM_FLIT_TYPE_BITS;

		flitData = (vcId << bitUsed) | flitData; // IConstants.CURRENT_VC_COUNT
		// bits for VCID
		bitUsed += IConstants_3D.NUM_VCID_BITS;

		data[0] = flitData; // 0xDDDDDD00 | flitData ;

		for (int i = 1; i < noOfInt; i++) {
			data[i] = Integer.MAX_VALUE - address; // (int)
			

		}
		Flit_3D dataFlit = new Flit_3D(data, curCycle);
		dataFlit.setDest(destination);
		dataFlit.setSource(this.address);

		return dataFlit;
	}


	protected int getMessageSize() {
		int noOfFlit;

		if (IConstants_3D.FIXED_MESSAGE_LENGTH == false) {
			noOfFlit = (int) (-8
					* IConstants_3D.AVG_MESSAGE_LENGTH
					* Math.log(NetworkManager_3D.getHelpingUtility()
							.getNextRandomNumber()) / IConstants_3D.FLIT_LENGTH);
			noOfFlit = noOfFlit > 1 ? noOfFlit : 2;
		}

		else {
			noOfFlit = 8 * IConstants_3D.AVG_MESSAGE_LENGTH
					/ IConstants_3D.FLIT_LENGTH;
			noOfFlit = noOfFlit > 1 ? noOfFlit : 2;
		}

		System.out.println("Min No Of Flits: " + noOfFlit);
		return noOfFlit;
		// return 10;
	}

	

	private int generateDestinationForMeshNetwork(int address) {
		int destination = address;
		int tempRow = 0, tempCol = 0, tempNode = 0;
		if (IConstants_3D.TRAFFIC_TYPE == IConstants_3D.TRAFFIC_UNIFORM) {
			destination = address;
			while (destination == address) {
				destination = (int) (NetworkManager_3D.getHelpingUtility()
						.getNextRandomNumber() * IConstants_3D.NUMBER_OF_IP_NODE);
				tempRow = destination
						/ (IConstants_3D.MESH_COL * IConstants_3D.MESH_ADJ_NODE);
				tempCol = (destination % (IConstants_3D.MESH_COL * IConstants_3D.MESH_ADJ_NODE))
						/ IConstants_3D.MESH_ADJ_NODE;
				tempNode = destination % IConstants_3D.MESH_ADJ_NODE;
				destination = (tempRow << (IConstants_3D.MESH_COL_BITS + IConstants_3D.MESH_NODE_BITS_REQ))
						+ (tempCol << IConstants_3D.MESH_NODE_BITS_REQ) + tempNode;
			}

			return destination;
		} else if (IConstants_3D.TRAFFIC_TYPE == IConstants_3D.TRAFFIC_LOCAL) {
			return -1;
		}
		return -1;
	}

	
	private int generateDestinationForTorusNetwork(int address) {
		int destination = address;
		int tempRow = 0, tempCol = 0, tempNode = 0;
		if (IConstants_3D.TRAFFIC_TYPE == IConstants_3D.TRAFFIC_UNIFORM) {
			destination = address;
			while (destination == address) {
				destination = (int) (NetworkManager_3D.getHelpingUtility()
						.getNextRandomNumber() * IConstants_3D.NUMBER_OF_IP_NODE);
				tempRow = destination
						/ (IConstants_3D.MESH_COL * IConstants_3D.MESH_ADJ_NODE);
				tempCol = (destination % (IConstants_3D.MESH_COL * IConstants_3D.MESH_ADJ_NODE))
						/ IConstants_3D.MESH_ADJ_NODE;
				tempNode = destination % IConstants_3D.MESH_ADJ_NODE;
				destination = (tempRow << (IConstants_3D.MESH_COL_BITS + IConstants_3D.MESH_NODE_BITS_REQ))
						+ (tempCol << IConstants_3D.MESH_NODE_BITS_REQ) + tempNode;
			}

			return destination;
		} else if (IConstants_3D.TRAFFIC_TYPE == IConstants_3D.TRAFFIC_LOCAL) {
			return -1;
		}
		return -1;
	}

	


	
}
