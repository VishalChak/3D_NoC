

public class InputLinkController_3D {

	private Switch_3D parentSwitch;

	
	private int linkNo;


	private int numVCCount;


	private int numLinkCount;

	
	private InputVCBuffer_3D inputBuffer;


	public InputLinkController_3D(Switch_3D parent, int linkNo, int vcCount,
			int linkCount) {
		this.parentSwitch = parent;
		this.linkNo = linkNo;
		this.numLinkCount = linkCount;
		this.numVCCount = vcCount;
		this.inputBuffer = new InputVCBuffer_3D(vcCount, linkNo);
	}

	

	public boolean addInputBufferData(Flit_3D flit, int curCycle) {
		int routeNo;

		if (IConstants_3D.HEADER_FLIT == flit.getType()) {
			if (inputBuffer.isVCFree(flit.getVirtualChannelNo())) {
				inputBuffer.addBufferData(flit, flit.getVirtualChannelNo(),
						curCycle);
				
				routeNo = parentSwitch.determineRoute(flit.getSource(),
						flit.getDest());
				inputBuffer.setRouteInfo(flit.getVirtualChannelNo(), routeNo);
			} else {
				System.out.println("Header Flit Loss " + flit.toString());
				return false;
			}
		} else if (inputBuffer.hasFreeSlotInVC(flit.getVirtualChannelNo())) {
			inputBuffer.addBufferData(flit, flit.getVirtualChannelNo(),
					curCycle);
		} else {
			System.out.println("Data Flit Loss " + flit.toString());
			return false;
		}
		return true;
	}

	
	public Flit_3D removeInputBufferData(int vcId, int curCycle) {
		return inputBuffer.removeBufferData(vcId, curCycle);
	}

	
	public boolean hasFreeSlotInVCBuffer(int vcId) {
		return inputBuffer.hasFreeSlotInVC(vcId);
	}

	public boolean isVCFree(int vcId) {
		return inputBuffer.isVCFree(vcId);
	}

	
	private int getFreeVC() {
		return inputBuffer.getFreeVC();
	}


	public InputVCBuffer_3D getInputBuffer() {
		return inputBuffer;
	}

	
	public void updateStatusAfterCycle() {
		inputBuffer.updateStatusAfterCycle();
	}

	
	public void setOutPathRequest(int curCycle) {
		int i, toLink;
		int newVC;

		for (i = 0; i < this.numVCCount; i++) {
			
			if ((null != this.inputBuffer.getBufferData(i))
					&& (this.inputBuffer.getBufferData(i)
							.getLastServiceTimeStamp() < curCycle)) {
				if (this.inputBuffer.getBufferData(i).getType() == IConstants_3D.HEADER_FLIT) {
					toLink = this.inputBuffer.getRouteInfo(i);

					
						newVC = this.parentSwitch.getOutputLinkController(
								toLink).getFreeVC();
					
					if (newVC >= 0) {
						parentSwitch.setSwitchingInfoVector(toLink * numVCCount
								+ newVC, this.linkNo * numVCCount + i);
						inputBuffer.setPathInfo(i, toLink * numVCCount + newVC);
					}

					
				} else {
					toLink = this.inputBuffer.getPathInfo(i) / numVCCount;
					int toVC = this.inputBuffer.getPathInfo(i) % numVCCount;
					if (this.parentSwitch.getOutputLinkController(toLink)
							.hasFreeSlotInVCBuffer(toVC))
						parentSwitch.setSwitchingInfoVector(this.inputBuffer
								.getPathInfo(i), this.linkNo * numVCCount + i);

				}
			}
		}
	}

}