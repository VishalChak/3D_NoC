
public class Flit_3D {
	
	private int[] data;

	
	private int genTimeStamp;

	
	private int lastServiceTimeStamp;

	
	private int src;

	
	private int dest;

	
	private int hop;

	public Flit_3D(int[] data, int genTimeStamp) {
		this.data = new int[data.length];
		this.genTimeStamp = genTimeStamp;
		this.lastServiceTimeStamp = genTimeStamp;
		this.hop = 0;
		for (int i = 0; i < data.length; i++) {
			this.data[i] = data[i];
		}
	}

	
	public void increaseHop() {
		hop++;
	}


	public int getHopCount() {
		return hop;
	}

	public int getType() {
		return this.data[0] & ((1 << IConstants_3D.NUM_FLIT_TYPE_BITS) - 1);
	}

	
	public int getVirtualChannelNo() {
		if (null == data) {
			return -1;
		}
		return (data[0] >> IConstants_3D.NUM_FLIT_TYPE_BITS)
				& ((1 << IConstants_3D.NUM_VCID_BITS) - 1);
	}

	
	public int getAddressLength() {
		int temp;
		int noOfBit = IConstants_3D.NUM_FLIT_TYPE_BITS + IConstants_3D.NUM_VCID_BITS;
		int noOfInt = noOfBit / IConstants_3D.INT_SIZE;
		int rest = IConstants_3D.INT_SIZE - (noOfBit % IConstants_3D.INT_SIZE);

		if (rest >= IConstants_3D.NUM_ADDR_BITS) {
			temp = (data[noOfInt] >>> (IConstants_3D.INT_SIZE - rest))
					& ((1 << IConstants_3D.NUM_ADDR_BITS) - 1);
		} else {
			temp = (data[noOfInt] >>> (IConstants_3D.INT_SIZE - rest))
					& ((1 << rest) - 1);
			temp = ((data[noOfInt + 1] & ((1 << (IConstants_3D.NUM_ADDR_BITS - rest)) - 1)) << rest)
					| temp;
		}

		return temp;
	}

	
	public int getPacketLength() {
		int temp;
		int noOfBit = IConstants_3D.NUM_FLIT_TYPE_BITS + IConstants_3D.NUM_VCID_BITS
				+ IConstants_3D.NUM_ADDR_BITS;
		int noOfInt = noOfBit / IConstants_3D.INT_SIZE;
		int rest = IConstants_3D.INT_SIZE - (noOfBit % IConstants_3D.INT_SIZE);

		if (rest >= IConstants_3D.NUM_FLITS_BITS) {
			temp = (data[noOfInt] >>> (IConstants_3D.INT_SIZE - rest))
					& ((1 << IConstants_3D.NUM_FLITS_BITS) - 1);
		} else {
			temp = (data[noOfInt] >>> (IConstants_3D.INT_SIZE - rest))
					& ((1 << rest) - 1);
			temp = ((data[noOfInt + 1] & ((1 << (IConstants_3D.NUM_FLITS_BITS - rest)) - 1)) << rest)
					| temp;
		}

		return temp;

	}

	
	public int getSourceNode() {
		int temp;
		int addrSize = getAddressLength();
		int noOfBit = IConstants_3D.NUM_FLIT_TYPE_BITS + IConstants_3D.NUM_VCID_BITS
				+ IConstants_3D.NUM_ADDR_BITS + IConstants_3D.NUM_FLITS_BITS;
		int noOfInt = noOfBit / IConstants_3D.INT_SIZE;
		int rest = IConstants_3D.INT_SIZE - (noOfBit % IConstants_3D.INT_SIZE);

		if (rest >= addrSize) {
			temp = (data[noOfInt] >>> (IConstants_3D.INT_SIZE - rest))
					& ((1 << addrSize) - 1);
		} else {
			temp = (data[noOfInt] >>> (IConstants_3D.INT_SIZE - rest))
					& ((1 << rest) - 1);
			temp = ((data[noOfInt + 1] & ((1 << (addrSize - rest)) - 1)) << rest)
					| temp;
		}

		return temp;

	}

	
	public int getDestinationNode() {

		int temp;
		int addrSize = getAddressLength();
		int noOfBit = IConstants_3D.NUM_FLIT_TYPE_BITS + IConstants_3D.NUM_VCID_BITS
				+ IConstants_3D.NUM_ADDR_BITS + IConstants_3D.NUM_FLITS_BITS
				+ addrSize;
		int noOfInt = noOfBit / IConstants_3D.INT_SIZE;
		int rest = IConstants_3D.INT_SIZE - (noOfBit % IConstants_3D.INT_SIZE);

		if (rest >= addrSize) {
			temp = (data[noOfInt] >>> (IConstants_3D.INT_SIZE - rest))
					& ((1 << addrSize) - 1);
		} else {
			temp = (data[noOfInt] >>> (IConstants_3D.INT_SIZE - rest))
					& ((1 << rest) - 1);
			temp = ((data[noOfInt + 1] & ((1 << (addrSize - rest)) - 1)) << rest)
					| temp;
		}
		return temp;
	}

	
	public int[] getData() {
		int[] temp = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			temp[i] = data[i];
		}
		temp[0] >>>= (IConstants_3D.NUM_FLIT_TYPE_BITS + IConstants_3D.NUM_VCID_BITS);
		return temp;
	}

	
	public String toString() {
		String temp = "";
		for (int i = 0; i < data.length; i++) {
			temp += Integer.toHexString(data[i]);
		}
		return temp;
	}

	
	public void setVirtualChannelNo(int vcId) {
		int mask = (1 << IConstants_3D.NUM_VCID_BITS) - 1;
		mask <<= IConstants_3D.NUM_FLIT_TYPE_BITS;
		mask = ~mask;
		data[0] &= mask;
		data[0] |= (vcId << IConstants_3D.NUM_FLIT_TYPE_BITS);
	}

	
	public void setLastServiceTimeStamp(int timeStamp) {
		this.lastServiceTimeStamp = timeStamp;
	}

	
	public int getLastServiceTimeStamp() {
		return this.lastServiceTimeStamp;
	}

	
	public void setGenTimeStamp(int timeStamp) {
		this.genTimeStamp = timeStamp;
	}

	

	public int getGenTimeStamp() {
		return this.genTimeStamp;
	}

	public int getSource() {
		return src;
	}

	
	public int getDest() {
		return dest;
	}

	
	public void setSource(int src) {
		this.src = src;
	}

	public void setDest(int dest) {
		this.dest = dest;
	}

}