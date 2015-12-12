
public interface Switch_3D {

	public abstract void setNoOfPhysicalLink(int no);

	
	public abstract int getNoOfPhysicalLink();

	
	public abstract void setNoOfVirtualLink(int noVlink);

	public abstract int getNoOfVirtualLink();

	
	public abstract void setAddress(int addr);

	public abstract int getAddress();

	
	public abstract void setAdjacentNode(Node_3D node, int linkNo);


	public abstract int getNumAdjacentNode();

	public abstract void setAdjacentSwitch(Switch_3D switchref, int linkNo);


	public abstract InputLinkController_3D getInputLinkController(int linkNo);


	public abstract void setInputLinkController(int linkNo,
			InputLinkController_3D iLC);


	public abstract OutputLinkController_3D getOutputLinkController(int linkNo);


	public abstract void setOutputLinkController(int linkNo,
			OutputLinkController_3D oLC);

	public abstract void resetSwitchingInfoVector();


	public abstract boolean addInputBufferData(int linkNo, Flit_3D flit,
			int curCycle);


	public abstract boolean addOutputBufferData(int linkNo, Flit_3D flit,
			int vcId, int curCycle);


	public abstract boolean setSwitchingInfoVector(int dest, int src);

	
	public abstract int getSwitchingInfoVector(int dest);

	public abstract Flit_3D removeInputBufferData(int linkNo, int vcId,
			int curCycle);

	
	public abstract Flit_3D removeOutputBufferData(int linkNo, int vcId,
			int curCycle);

	public abstract int determineRoute(int src, int dest);

	
	public abstract boolean hasFreeSlotInVCBuffer(int linkNo, int vcId);

	
	public abstract void updateSwitchOutPathRequest(int curCycle);

	
	public abstract void moveInputBufferToOutputBuffer(int curCycle);

	
	public abstract void moveSwitchOutputBufferToInputBufferOfNodeSwitch(
			int curCycle);

	
	public abstract boolean isVCFreeInSwitch(int linkNo, int vcId);

	public abstract int getNumLinkActive();

	public abstract void updateStatusAfterCycle(int curCycle);


	public abstract void createRouter();

}
