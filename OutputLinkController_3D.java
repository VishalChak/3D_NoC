
public class OutputLinkController_3D {
	
	private Switch_3D parentSwitch;

	
	private OutputVCBuffer_3D outputBuffer;

	
	private int linkNo;

	
	private int numVCCount;


	private int numLinkCount;

	
	public OutputLinkController_3D(Switch_3D parent, int linkNo, int vcCount,
			int linkCount) {
		this.parentSwitch = parent;
		this.outputBuffer = new OutputVCBuffer_3D(vcCount, linkNo);
		this.linkNo = linkNo;
		this.numVCCount = vcCount;
		this.numLinkCount = linkCount;

	}

	
	public boolean addOutputBufferData(Flit_3D flit, int vcId, int curCycle) {
		return outputBuffer.addBufferData(flit, vcId, curCycle);
	}

	
	public Flit_3D removeOutputBufferData(int vcId, int curCycle) {
		return outputBuffer.removeBufferData(vcId, curCycle);
	}

	
	public boolean hasFreeSlotInVCBuffer(int vcId) {
		return outputBuffer.hasFreeSlotInVC(vcId);
	}


	public boolean hasFlitToSend(int vcId) {
		return outputBuffer.hasFlitToSend(vcId);
	}

	public void updateStatusAfterCycle() {
		outputBuffer.updateStatusAfterCycle();
	}


	public Flit_3D getBufferData(int vcId) {
		return outputBuffer.getBufferData(vcId);
	}

	public int getFreeVC() {
		return outputBuffer.getFreeVC();
	}


	public int getFreeVC_NEW(Flit_3D vc) {
		return outputBuffer.getFreeVC_NEW(vc);
	}


	public OutputVCBuffer_3D getOutputBuffer() {
		return outputBuffer;
	}


}
