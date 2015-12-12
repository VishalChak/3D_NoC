import java.util.*;



public class InputVCBuffer_3D {
	
	private Vector data[];

	
	private int vcCount;

	
	private int pLinkNo;

	
	private int[] routeInfo;

	
	private int[] pathInfo;


	private int[] flitCounter;

	
	private boolean buffMidStatus[];

	
	public InputVCBuffer_3D(int linkNo) {
		this(IConstants_3D.DEFAULT_VC_COUNT, linkNo);
	}

	
	public InputVCBuffer_3D(int vcCount, int linkNo) {
		this.pLinkNo = linkNo;
		this.vcCount = vcCount;
		data = new Vector[vcCount];
		routeInfo = new int[vcCount];
		pathInfo = new int[vcCount];
		buffMidStatus = new boolean[vcCount];
		flitCounter = new int[vcCount];

		for (int i = 0; i < vcCount; i++) {
			data[i] = new Vector();
			data[i].ensureCapacity(IConstants_3D.NUM_FLIT_PER_BUFFER);
			routeInfo[i] = -1;
			pathInfo[i] = -1;
			buffMidStatus[i] = false;
			flitCounter[i] = 0;
		}
	}

	public boolean addBufferData(Flit_3D flit, int vcId, int curCycle) {
		// header flit. So have to keep track of how many more flit is to
		// process.
		if (flit.getType() == IConstants_3D.HEADER_FLIT) {
			flitCounter[vcId] = flit.getPacketLength();
		}
		flit.setLastServiceTimeStamp(curCycle);
		flit.increaseHop();
		data[vcId].add(flit);
		return true;
	}


	public Flit_3D removeBufferData(int vcId, int curCycle) {
		Flit_3D flit = (Flit_3D) data[vcId].firstElement();
		if (flit.getLastServiceTimeStamp() < curCycle) {
			flit = (Flit_3D) data[vcId].remove(0);
			flit.setLastServiceTimeStamp(curCycle);
			buffMidStatus[vcId] = true; // the storage corresponding to this can
			
			return flit;
		}
		return null;
	}

	
	public Flit_3D getBufferData(int vcId) {
		if (data[vcId].size() == 0) {
			return null;
		}
		return (Flit_3D) data[vcId].firstElement();

	}

	
	public void updateStatusAfterCycle() {
		for (int i = 0; i < vcCount; i++) {
			if (buffMidStatus[i] == true) {
				flitCounter[i]--;
				if (flitCounter[i] == 0) {
					routeInfo[i] = -1;
					pathInfo[i] = -1;
				}
				buffMidStatus[i] = false;
			}
		}
	}

	

	public boolean isVCFree(int vcId) {
		if ((buffMidStatus[vcId] == false) && (flitCounter[vcId] == 0)) {
			return true;
		}
		return false;
	}

	
	public boolean hasFreeSlotInVC(int vcId) {
		if ((data[vcId].size() < IConstants_3D.NUM_FLIT_PER_BUFFER)
				&& (buffMidStatus[vcId] == false)) {
			return true;
		} else if (((data[vcId].size() + 1) < IConstants_3D.NUM_FLIT_PER_BUFFER)
				&& (buffMidStatus[vcId] == true)) {
			return true;
		}
		return false;
	}

	
	public void setRouteInfo(int vcId, int dest) {
		routeInfo[vcId] = dest;
	}

	
	public int getRouteInfo(int vcId) {
		return routeInfo[vcId];
	}

	
	public void resetRouteInfo(int vcId) {
		routeInfo[vcId] = -1;
	}

	
	public int getFreeVC() {
		for (int i = 0; i < vcCount; i++) {
			if ((buffMidStatus[i] == false) && (flitCounter[i] == 0)) {
				return i;
			}
		}
		return -1;
	}

	
	public boolean hasFlitToSend(int vcId) {
		return data[vcId].size() > 0;
	}

	
	public void setPathInfo(int vcId, int dest) {
		pathInfo[vcId] = dest;
	}

	public int getPathInfo(int vcId) {
		return pathInfo[vcId];
	}

	
	public int getNumSlotUsed() {
		int i, count = 0;
		for (i = 0; i < this.vcCount; i++) {
			count += data[i].size();
		}
		return count;
	}

}
