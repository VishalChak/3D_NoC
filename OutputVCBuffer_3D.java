import java.util.*;


public class OutputVCBuffer_3D {

	private Vector data[];

	
	private int pLinkNo;

	
	private int numVCCount;

	
	private int[] flitCounter; 

	
	private boolean buffMidStatus[];

	
	private boolean buffAssignedStatus[];

	public OutputVCBuffer_3D(int linkNo) {
		this(IConstants_3D.DEFAULT_VC_COUNT, linkNo);
	}

	
	public OutputVCBuffer_3D(int vcCount, int linkNo) {
		this.numVCCount = vcCount;
		this.pLinkNo = linkNo;
		data = new Vector[vcCount];
		flitCounter = new int[vcCount];
		buffMidStatus = new boolean[vcCount];
		buffAssignedStatus = new boolean[vcCount];

		
		for (int i = 0; i < vcCount; i++) {
			data[i] = new Vector();
			data[i].ensureCapacity(IConstants_3D.NUM_FLIT_PER_BUFFER);
			flitCounter[i] = 0;
			buffMidStatus[i] = false;
			buffAssignedStatus[i] = false;

			
		}
	}

	

	public boolean addBufferData(Flit_3D flit, int vcId, int curCycle) {
		
		if (flit.getType() == IConstants_3D.HEADER_FLIT) {
			flitCounter[vcId] = flit.getPacketLength();
		}
		flit.setVirtualChannelNo(vcId);
		flit.setLastServiceTimeStamp(curCycle);
		data[vcId].add(flit);
		return true;
	}

	
	public Flit_3D removeBufferData(int vcId, int curCycle) {
		Flit_3D flit = (Flit_3D) data[vcId].get(0);
		if (flit.getLastServiceTimeStamp() < curCycle) {
			flit = (Flit_3D) data[vcId].remove(0);
			flit.setLastServiceTimeStamp(curCycle);
			buffMidStatus[vcId] = true; 
			
			return flit;
		}
		return null;
	}

	
	public void updateStatusAfterCycle() {
		for (int i = 0; i < numVCCount; i++) {
			if (buffMidStatus[i] == true) {
				flitCounter[i]--;
				if (flitCounter[i] == 0) {
					
				}
				buffMidStatus[i] = false;
			}
		}
		for (int i = 0; i < numVCCount; i++) {
			buffAssignedStatus[i] = false;

		}
	}

	
	public Flit_3D getBufferData(int vcId) {
		if (data[vcId].size() == 0) {
			return null;
		}
		return (Flit_3D) data[vcId].firstElement();
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


	public int getFreeVC() {
		for (int i = 0; i < numVCCount; i++) {
			if ((buffAssignedStatus[i] == false) && (flitCounter[i] == 0)) {
				buffAssignedStatus[i] = true;
				return i;
			}
		}
		return -1;
	}

	
	public int getFreeVC_NEW(Flit_3D f) {
		int i = f.getHopCount();

		if (i >= this.numVCCount) {
			i = this.numVCCount - 1;
		}

		if ((buffAssignedStatus[i] == false) && (flitCounter[i] == 0)) {
			buffAssignedStatus[i] = true;
			return i;
		}

		else
			return -1;
	}

	
	public boolean hasFlitToSend(int vcId) {
		return data[vcId].size() > 0;
	}

	
	public int getNumUsedVC() {
		int count = 0;
		for (int i = 0; i < numVCCount; i++) {
			if ((buffAssignedStatus[i] == true) || (flitCounter[i] > 0)) {
				count++;
			}
		}
		return count;
	}

	
	public int getNumSlotUsed() {
		int i, count = 0;
		for (i = 0; i < this.numVCCount; i++) {
			count += data[i].size();
		}
		if (IConstants_3D.CURRENT_VC_COUNT * IConstants_3D.NUM_FLIT_PER_BUFFER < count)
			System.out.println("\n\nBuf Over Use\n\n");
		return count;
	}
}
