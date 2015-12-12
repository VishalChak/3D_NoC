import java.io.*;

public class MeshSwitch_3D implements Switch_3D {

	
	private int address;

	
	private int switchIndex;

	
	private int noOfPhysicalLink;

	
	private int noOfVirtualLink;

	
	private MeshSwitch_3D[] switchList;

	
	private Node_3D[] nodeList;

	
	private int[] lastVCServedList;

	
	private InputLinkController_3D inputLC[];

	
	private OutputLinkController_3D outputLC[];

	
	private boolean LinkUseStatus[];

	
	private Router_3D router;

	
	private int[] switchingInfoVector;

	
	public void setNoOfPhysicalLink(int no) {
		this.noOfPhysicalLink = no;
	}

	
	public int getNoOfPhysicalLink() {
		return this.noOfPhysicalLink;
	}

	
	public void setNoOfVirtualLink(int noVlink) {
		this.noOfVirtualLink = noVlink;
	}

	
	public int getNoOfVirtualLink() {
		return this.noOfVirtualLink;
	}

	
	public void setAddress(int addr) {
		this.address = addr;
	}

	
	public int getAddress() {
		return this.address;
	}

	
	public MeshSwitch_3D(int pLink, int vLink, int address, int noOfAdjNode,
			int noOfAdjSwitch, int switchIndex) {
		int i;

		setNoOfPhysicalLink(pLink);
		setNoOfVirtualLink(vLink);
		setAddress(address);
		this.switchIndex = switchIndex;
		createLinkController(noOfPhysicalLink, noOfVirtualLink);
		createSwitchingInfoVector();

		nodeList = new Node_3D[noOfAdjNode];
		switchList = new MeshSwitch_3D[noOfAdjSwitch];
		createRouter();

		lastVCServedList = new int[noOfAdjNode + noOfAdjSwitch];// track which
		
		for (i = 0; i < noOfAdjNode + noOfAdjSwitch; i++) {
			lastVCServedList[i] = 0;
		}
		if (IConstants_3D.TRACE) {
			try {
				RandomAccessFile raf = new RandomAccessFile(
						IConstants_3D.TRACE_FILE, "rw");
				raf.seek(raf.length());
				raf.writeBytes("\nCreated Mesh/Torus Switch address = "
						+ this.address);
				raf.close();
			} catch (IOException ioex) {
			}
		}

	}

	
	public void createRouter() {
		if (IConstants_3D.NET_MESH == IConstants_3D.CURRENT_NET)
			router = new MeshRouter_3D();
		
	}

	
	private void createLinkController(int linkCount, int vcCount) {
		inputLC = new InputLinkController_3D[linkCount];
		outputLC = new OutputLinkController_3D[linkCount];
		LinkUseStatus = new boolean[linkCount];

		for (int i = 0; i < linkCount; i++) {
			inputLC[i] = new InputLinkController_3D(this, i, vcCount, linkCount);
			outputLC[i] = new OutputLinkController_3D(this, i, vcCount, linkCount);
			LinkUseStatus[i] = false;
		}
	}


	public void setAdjacentNode(Node_3D node, int linkNo) {
		nodeList[linkNo] = node;
	}

	
	public int getNumAdjacentNode() {
		int i, count = 0;
		for (i = 0; i < nodeList.length; i++) {
			if (null != nodeList[i]) {
				count++;
			}
		}
		return count;
	}


	public void setAdjacentSwitch(MeshSwitch_3D meshSwitch, int linkNo) {
		switchList[linkNo] = meshSwitch;
	}


	public InputLinkController_3D getInputLinkController(int linkNo) {
		return inputLC[linkNo];
	}

	public void setInputLinkController(int linkNo, InputLinkController_3D iLC) {
		inputLC[linkNo] = iLC;
	}


	public OutputLinkController_3D getOutputLinkController(int linkNo) {
		return outputLC[linkNo];
	}

	public void setOutputLinkController(int linkNo, OutputLinkController_3D oLC) {
		outputLC[linkNo] = oLC;
	}


	private void createSwitchingInfoVector() {
		switchingInfoVector = new int[noOfPhysicalLink * noOfVirtualLink];
		resetSwitchingInfoVector();
	}


	public void resetSwitchingInfoVector() {
		int dim = noOfPhysicalLink * noOfVirtualLink;
		for (int i = 0; i < dim; i++) {
			switchingInfoVector[i] = -1;
		}
	}

	
	public boolean addInputBufferData(int linkNo, Flit_3D flit, int curCycle) {
		return inputLC[linkNo].addInputBufferData(flit, curCycle);
	}


	public boolean addOutputBufferData(int linkNo, Flit_3D flit, int vcId,
			int curCycle) {
		return outputLC[linkNo].addOutputBufferData(flit, vcId, curCycle);
	}


	public boolean setSwitchingInfoVector(int dest, int src) {
		if (switchingInfoVector[dest] < 0) {
			switchingInfoVector[dest] = src;
			return true;
		}
		return false;
	}


	public int getSwitchingInfoVector(int dest) {
		return switchingInfoVector[dest];
	}


	public Flit_3D removeInputBufferData(int linkNo, int vcId, int curCycle) {
		return inputLC[linkNo].removeInputBufferData(vcId, curCycle);
	}


	public Flit_3D removeOutputBufferData(int linkNo, int vcId, int curCycle) {
		return outputLC[linkNo].removeOutputBufferData(vcId, curCycle);
	}


	public int determineRoute(int src, int dest) {
		
		return router.determineRoute(src, dest, this.address);
	}


	public boolean hasFreeSlotInVCBuffer(int linkNo, int vcId) {
		return inputLC[linkNo].hasFreeSlotInVCBuffer(vcId);
	}

	
	public void updateSwitchOutPathRequest(int curCycle) {
		int i;
		for (i = 0; i < noOfPhysicalLink; i++) {
			if (null != inputLC[i]) {
				inputLC[i].setOutPathRequest(curCycle);
			}
		}
	}

	public void moveInputBufferToOutputBuffer(int curCycle) {
		int i, numPLVC = noOfPhysicalLink * noOfVirtualLink;
		int srcLinkNo, srcVCId, destLinkNo, destVCId;
		Flit_3D flit;

		for (i = 0; i < numPLVC; i++) {
			if (switchingInfoVector[i] >= 0) {
				destLinkNo = i / noOfVirtualLink;
				destVCId = i % noOfVirtualLink;

				srcLinkNo = switchingInfoVector[i] / noOfVirtualLink;
				srcVCId = switchingInfoVector[i] % noOfVirtualLink;

				flit = inputLC[srcLinkNo].removeInputBufferData(srcVCId,
						curCycle);
				flit.setLastServiceTimeStamp(curCycle);

				outputLC[destLinkNo].addOutputBufferData(flit, destVCId,
						curCycle);
				if (IConstants_3D.TRACE) {
					try {
						RandomAccessFile raf = new RandomAccessFile(
								IConstants_3D.TRACE_FILE, "rw");
						raf.seek(raf.length());
						if (IConstants_3D.HEADER_FLIT == flit.getType()) {
							raf
									.writeBytes("\nCycle " + curCycle + " ( "
											+ flit.getSource() + ","
											+ flit.getDest() + ") "
											+ " Header Flit("
											+ flit.getSourceNode() + ","
											+ flit.getDestinationNode()
											+ ") is SWITCHING from Link ("
											+ srcLinkNo + "," + srcVCId
											+ ") to (" + destLinkNo + ","
											+ destVCId + ") at Switch "
											+ address);

						} else {
							raf.writeBytes("\nCycle " + curCycle + " ( "
									+ flit.getSource() + "," + flit.getDest()
									+ ") "
									+ " Data Flit is SWITCHING from Link ("
									+ srcLinkNo + "," + srcVCId + ") to ("
									+ destLinkNo + "," + destVCId
									+ ") at Switch " + address);

						}

						raf.close();
					} catch (IOException ioex) {
					}
				}
			}
		}
	}

	
	private void forwardFlitToSwitch(MeshSwitch_3D adjSwitch, int linkNo,
			int curCycle) {
		int count = 0, toLink, apLink, numNode;
		Flit_3D flit;

		numNode = IConstants_3D.MESH_ADJ_NODE;
		apLink = (int) (linkNo - numNode); 
		lastVCServedList[linkNo] = (int) ((++lastVCServedList[linkNo]) % noOfVirtualLink);
		while (count < noOfVirtualLink) {
			if ((outputLC[linkNo].hasFlitToSend(lastVCServedList[linkNo]))
					&& (outputLC[linkNo]
							.getBufferData(lastVCServedList[linkNo])
							.getLastServiceTimeStamp() < curCycle)) {
				toLink = (int) (getMeshSwitchOutAdjLinkNo(apLink) + numNode);
				if (outputLC[linkNo].getBufferData(lastVCServedList[linkNo])
						.getType() == IConstants_3D.HEADER_FLIT) {
					
					if (adjSwitch.isVCFreeInSwitch(toLink,
							lastVCServedList[linkNo])) {
						flit = outputLC[linkNo].removeOutputBufferData(
								lastVCServedList[linkNo], curCycle);
						flit.setLastServiceTimeStamp(curCycle);
						if (IConstants_3D.TRACE) {
							try {
								RandomAccessFile raf = new RandomAccessFile(
										IConstants_3D.TRACE_FILE, "rw");
								raf.seek(raf.length());
								raf.writeBytes("\nCycle " + curCycle + " ( "
										+ flit.getSource() + ","
										+ flit.getDest() + ") "
										+ " Header Flit("
										+ flit.getSourceNode() + ","
										+ flit.getDestinationNode()
										+ ") Length " + flit.getPacketLength()
										+ " is MOVING from SWITCH " + address
										+ " TO SWITCH "
										+ adjSwitch.getAddress()
										+ " from (Link,VC) =  (" + linkNo + ","
										+ lastVCServedList[linkNo]
										+ ") to (Link,VC) = " + toLink + ","
										+ lastVCServedList[linkNo] + ")");

								raf.close();
							} catch (IOException ioex) {
							}
						}

						if (curCycle > IConstants_3D.WARM_UP_CYCLE)
							NetworkManager_3D.getStatDataInstance()
									.incrementFlitLeavingFromSwitch(
											this.switchIndex);
						LinkUseStatus[linkNo] = true;// for Link Utilization
						// flit.increaseHop();
						adjSwitch.addInputBufferData(toLink, flit, curCycle);
						break;
					}
					
				} else {
					// data flit. need a free slot in VC buffer
					if (adjSwitch.hasFreeSlotInVCBuffer(toLink,
							lastVCServedList[linkNo])) {
						flit = outputLC[linkNo].removeOutputBufferData(
								lastVCServedList[linkNo], curCycle);
						flit.setLastServiceTimeStamp(curCycle);

						if (IConstants_3D.TRACE) {
							try {
								RandomAccessFile raf = new RandomAccessFile(
										IConstants_3D.TRACE_FILE, "rw");
								raf.seek(raf.length());
								raf.writeBytes("\nCycle " + curCycle + " ( "
										+ flit.getSource() + ","
										+ flit.getDest() + ") "
										+ " Data Flit is MOVING from SWITCH "
										+ address + " TO SWITCH "
										+ adjSwitch.getAddress()
										+ "  from (Link,VC) =  (" + linkNo
										+ "," + lastVCServedList[linkNo]
										+ ") to (Link,VC) = " + toLink + ","
										+ lastVCServedList[linkNo] + ")");

								raf.close();
							} catch (IOException ioex) {
							}
						}

						if (curCycle > IConstants_3D.WARM_UP_CYCLE)
							NetworkManager_3D.getStatDataInstance()
									.incrementFlitLeavingFromSwitch(
											this.switchIndex);
						LinkUseStatus[linkNo] = true;// for Link Utilization
						// flit.increaseHop();
						adjSwitch.addInputBufferData(toLink, flit, curCycle);
						break;
					}
				}

			}
			lastVCServedList[linkNo] = (int) ((++lastVCServedList[linkNo]) % noOfVirtualLink);
			count++;
			

		}
	}

	
	private void forwardFlitToNode(Node_3D adjNode, int linkNo, int curCycle) {
		int count = 0;
		Flit_3D flit;

		lastVCServedList[linkNo] = (int) ((++lastVCServedList[linkNo]) % noOfVirtualLink);
		while (count < noOfVirtualLink) {
			if (outputLC[linkNo].hasFlitToSend(lastVCServedList[linkNo])
					&& outputLC[linkNo].getBufferData(lastVCServedList[linkNo])
							.getLastServiceTimeStamp() < curCycle
					&& adjNode.getLastUsedOwnInCycle() < (NetworkManager_3D
							.getHelpingUtility().getConvertedCycle(curCycle,
									adjNode.getClockRateFactor()) + 1)) {
				// one VC found to send flit
				if (outputLC[linkNo].getBufferData(lastVCServedList[linkNo])
						.getType() == IConstants_3D.HEADER_FLIT) {
					// header flit. So need a free VC
					if (adjNode.isInputVCFree(lastVCServedList[linkNo])) {
						adjNode
								.setLastUsedOwnInCycle(NetworkManager_3D
										.getHelpingUtility().getConvertedCycle(
												curCycle,
												adjNode.getClockRateFactor()) + 1);

						flit = outputLC[linkNo].removeOutputBufferData(
								lastVCServedList[linkNo], curCycle);
						flit.setLastServiceTimeStamp(curCycle);
						if (IConstants_3D.TRACE) {
							try {
								RandomAccessFile raf = new RandomAccessFile(
										IConstants_3D.TRACE_FILE, "rw");
								raf.seek(raf.length());
								raf.writeBytes("\nNode " + adjNode.getAddress()
										+ " In Cycle "
										+ adjNode.getLastUsedOwnInCycle()
										+ " Switch Cycle " + curCycle
										+ " Header Flit("
										+ flit.getSourceNode() + ","
										+ flit.getDestinationNode()
										+ ") is MOVING from SWITCH " + address
										+ " TO NODE " + adjNode.getAddress()
										+ " at (Link,VC) = (" + linkNo + ","
										+ lastVCServedList[linkNo] + ")");

								raf.close();
							} catch (IOException ioex) {
							}
						}
						adjNode.addInputBufferData(flit, curCycle);
						if (curCycle > IConstants_3D.WARM_UP_CYCLE)
							NetworkManager_3D.getStatDataInstance()
									.incrementFlitLeavingFromSwitch(
											this.switchIndex);
						LinkUseStatus[linkNo] = true;// for Link Utilization
						break;
					}
				} else {
					// data flit. need a free slot in VC buffer
					if (adjNode.hasFreeSlotInInputVC(lastVCServedList[linkNo])) {
						adjNode
								.setLastUsedOwnInCycle(NetworkManager_3D
										.getHelpingUtility().getConvertedCycle(
												curCycle,
												adjNode.getClockRateFactor()) + 1);

						flit = outputLC[linkNo].removeOutputBufferData(
								lastVCServedList[linkNo], curCycle);
						flit.setLastServiceTimeStamp(curCycle);
						if (IConstants_3D.TRACE) {
							try {
								RandomAccessFile raf = new RandomAccessFile(
										IConstants_3D.TRACE_FILE, "rw");
								raf.seek(raf.length());
								raf.writeBytes("\nNode " + adjNode.getAddress()
										+ " In Cycle "
										+ adjNode.getLastUsedOwnInCycle()
										+ " Switch Cycle " + curCycle
										+ " Data Flit is MOVING from SWITCH "
										+ address + " TO NODE "
										+ adjNode.getAddress()
										+ " at (Link,VC) = (" + linkNo + ","
										+ lastVCServedList[linkNo] + ")");

								raf.close();
							} catch (IOException ioex) {
							}
						}

						adjNode.addInputBufferData(flit, curCycle);
						if (curCycle > IConstants_3D.WARM_UP_CYCLE)
							NetworkManager_3D.getStatDataInstance()
									.incrementFlitLeavingFromSwitch(
											this.switchIndex);
						LinkUseStatus[linkNo] = true;// for Link Utilization
						break;
					}

				}

			}

			lastVCServedList[linkNo] = (int) ((++lastVCServedList[linkNo]) % noOfVirtualLink);
			count++;
			

		}
	}

	
	public void moveSwitchOutputBufferToInputBufferOfNodeSwitch(int curCycle) {
		int i;
		Node_3D adjNode;
		MeshSwitch_3D adjSwitch;

		
		for (i = 0; i < noOfPhysicalLink; i++) {
			
			if (i < nodeList.length) {
				adjNode = nodeList[i];
				if (null != adjNode) {
					forwardFlitToNode(adjNode, i, curCycle);
				}
			}
			
			else {
				int link = (int) (i - nodeList.length);
				adjSwitch = switchList[link];
				if (null != adjSwitch) {
					forwardFlitToSwitch(adjSwitch, i, curCycle);
				}
			}
		}
	}


	public boolean isVCFreeInSwitch(int linkNo, int vcId) {
		if (null != inputLC[linkNo]) {
			return inputLC[linkNo].isVCFree(vcId);
		} else {
			return false;
		}
	}


	public int getNumLinkActive() {
		int count = 0;
		for (int i = 0; i < this.noOfPhysicalLink; i++)
			if (null != outputLC[i])
				count++;
		return count;
	}


	public void updateStatusAfterCycle(int curCycle) {
		int i, useCount = 0, inBufUsed = 0, outBufUsed = 0;
		for (i = 0; i < noOfPhysicalLink; i++) {
			if (null != inputLC[i]) {
				inputLC[i].updateStatusAfterCycle();
				outputLC[i].updateStatusAfterCycle();

				// stat
				inBufUsed += inputLC[i].getInputBuffer().getNumSlotUsed();
				outBufUsed += outputLC[i].getOutputBuffer().getNumSlotUsed();

				if (LinkUseStatus[i] == true) {
					useCount++;
					LinkUseStatus[i] = false;
				}
			}
		}
		resetSwitchingInfoVector();

		// stat
		if (curCycle > IConstants_3D.WARM_UP_CYCLE) {
			NetworkManager_3D.getStatDataInstance().incrementSwitchLinkUse(
					this.switchIndex, useCount);

			NetworkManager_3D.getStatDataInstance().incrementSwitchInputBufferUse(
					this.switchIndex, inBufUsed);
			NetworkManager_3D.getStatDataInstance()
					.incrementSwitchOutputBufferUse(this.switchIndex,
							outBufUsed);
		}

	}


	private int getMeshSwitchOutAdjLinkNo(int linkNo) {
		if (linkNo == IConstants_3D.SWITCH_LEFT) {
			return IConstants_3D.SWITCH_RIGHT;
		} else if (linkNo == IConstants_3D.SWITCH_TOP) {
			return IConstants_3D.SWITCH_BOTTOM;
		}

		else if (linkNo == IConstants_3D.SWITCH_RIGHT) {
			return IConstants_3D.SWITCH_LEFT;
		}

		else if (linkNo == IConstants_3D.SWITCH_BOTTOM) {
			return IConstants_3D.SWITCH_TOP;
		}

		else
			return -1;
	}

	public void setAdjacentSwitch(Switch_3D meshSwitch, int linkNo) {
		switchList[linkNo] = (MeshSwitch_3D) meshSwitch;
	}

}