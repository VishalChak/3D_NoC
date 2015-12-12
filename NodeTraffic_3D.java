import java.util.*;



abstract class NodeTraffic_3D {
	
	protected int address;

	
	protected int nextMsgGenTime;

	
	public NodeTraffic_3D(int nodeAddress) {
		address = nodeAddress;
	}


	abstract public Vector generateMessage(int curCycle, int curMessageCount);


	abstract public void setNextMsgGenTime(int curCycle);

	abstract protected int getNextMsgGenTime();

	
	abstract protected int getDestination();

	
	abstract protected int getMessageSize();

	
	abstract protected Flit_3D createHeaderFlit(int destination, int noOfFlit,
			int vcId, int curCycle);

	abstract protected Flit_3D createDataFlit(int destination, int vcId,
			int curCycle);
}
