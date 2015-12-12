import java.io.*;
import java.util.*;


public class Node_3D {

	
	
	private final int address;
	
	

	public Switch_3D parent;

	
	private int messageVCIndex[];

	
	private int messageCount = 0;


	private Vector[] messageList;


	private Vector[] receiveMessageList;


	private int nodeReceivedFlitCounter[];


	private int[] outVCUsedList;

	
	private int vcCount;

	
	private int linkNo;

	private int lastSender = 0;


	private int lastOutVCServed = 0;

	
	private int lastInVCServed = 0;

	
	private int nodeListIndex;


	private boolean linkUsed = false;

	
	private double clockRateFactor;

	
	private int lastUsedOwnInCycle = -1;

	
	private int lastUsedOwnOutCycle = -1;

	
	private InputVCBuffer_3D inputBuffer;

	
	private OutputVCBuffer_3D outputBuffer;

	
	NodeTraffic_3D nodeTraffic;

	public Node_3D(int address, Switch_3D parent, int pLink, int vcCount,
			double clkRateFactor) {
		int i;
		this.address = address;
		
		this.parent = parent;
		this.linkNo = pLink;
		this.vcCount = vcCount;
		this.clockRateFactor = clkRateFactor;
		messageVCIndex = new int[IConstants_3D.MAX_MESSAGE_NUMBER];

		messageList = new Vector[IConstants_3D.MAX_MESSAGE_NUMBER];
		receiveMessageList = new Vector[vcCount];
		nodeReceivedFlitCounter = new int[vcCount];

		outVCUsedList = new int[vcCount];
		// inVCUsedList = new int[vcCount];

		inputBuffer = new InputVCBuffer_3D(vcCount, 0);
		outputBuffer = new OutputVCBuffer_3D(vcCount, 0);

		for (i = 0; i < IConstants_3D.MAX_MESSAGE_NUMBER; i++) {
			messageVCIndex[i] = -1;
		}

		for (i = 0; i < vcCount; i++) {
			outVCUsedList[i] = 0;
			// inVCUsedList[i] = 0;
		}

		nodeTraffic = new ConcreteNodeTraffic_3D(address);

	}

	public void setClockRateFactor(double clkRateFactor) {
		this.clockRateFactor = clkRateFactor;
	}


	public double getClockRateFactor() {
		return this.clockRateFactor;
	}


	public void setLastUsedOwnInCycle(int cycle) {
		this.lastUsedOwnInCycle = cycle;
	}

	public int getLastUsedOwnInCycle() {
		return this.lastUsedOwnInCycle;
	}


	public void setLastUsedOwnOutCycle(int cycle) {
		this.lastUsedOwnOutCycle = cycle;
	}

	public int getLastUsedOwnOutCycle() {
		return this.lastUsedOwnOutCycle;
	}

	
	public void setAddress(int addr) {
		//this.address = addr;
	}

	
	public int getAddress() {
		return this.address;
	}


	private void generateMessage(int curCycle) {
		Vector packet;
		int i;

		packet = nodeTraffic.generateMessage(curCycle, messageCount);

		if (packet == null) {
			if (curCycle > IConstants_3D.WARM_UP_CYCLE) {
				NetworkManager_3D.getStatDataInstance()
						.incrementMessageNotProduced(nodeListIndex);

			}
			return;
		}

		i = 0;
		while (i < IConstants_3D.MAX_MESSAGE_NUMBER && messageList[i] != null) {
			i++;
		}

		if (i < IConstants_3D.MAX_MESSAGE_NUMBER && messageList[i] == null) {
			this.messageList[i] = packet;
			this.messageCount++;
		}
		if (curCycle > IConstants_3D.WARM_UP_CYCLE) {
			NetworkManager_3D.getStatDataInstance().incrementPacketProduced(
					this.nodeListIndex);
		}

	}

	
	private int getNumUsedVC() {
		int i, num = 0;
		for (i = 0; i < vcCount; i++) {
			if (outVCUsedList[i] != 0) {
				num++;
			}
		}
		return num;
	}

	public void updateOutput(int curCycle) {
		if (curCycle == nodeTraffic.getNextMsgGenTime()) {
			generateMessage(curCycle);
			// setNextMsgGenTime(curCycle) ;
		}

		checkForMsgFreeVC();
		fillEmptyBuffer(curCycle);
		forwardFlitToSwitch(curCycle);
	}


	private void checkForMsgFreeVC() {
		int count, vc;
		int numUsedVC = 0;

		numUsedVC = outputBuffer.getNumUsedVC();
		// Free VC available
		if (numUsedVC < vcCount) {
			count = 0;
			while (count < IConstants_3D.MAX_MESSAGE_NUMBER) {
				// a meesage that is looking for VC is found
				if (messageList[lastSender] != null
						&& messageVCIndex[lastSender] < 0) {
					// get the free VC and assign this VC to the meesage.
					vc = outputBuffer.getFreeVC();
					if (vc >= 0) {
						messageVCIndex[lastSender] = vc;
						outVCUsedList[vc] = 1;
					}
					break;
				} else {
					lastSender = (int) ((lastSender + 1) % IConstants_3D.MAX_MESSAGE_NUMBER);
				}
				count++;
			}
		}
	}


	private void fillEmptyBuffer(int curCycle) {
		int i = 0;
		Vector packet;
		Flit_3D flit;
		for (i = 0; i < IConstants_3D.MAX_MESSAGE_NUMBER; i++) {
			
			if (messageVCIndex[i] >= 0) { // &&
			
				packet = (Vector) messageList[i];
				while (packet.size() > 0
						&& outputBuffer.hasFreeSlotInVC(messageVCIndex[i])) {
					flit = (Flit_3D) packet.remove(0);
					

					flit.setLastServiceTimeStamp(curCycle);
					
					/** ** Eliminated *** */

					outputBuffer.addBufferData(flit, messageVCIndex[i],
							curCycle);
					if (IConstants_3D.HEADER_FLIT == flit.getType()) {
						if (curCycle > IConstants_3D.WARM_UP_CYCLE) {
							NetworkManager_3D.getStatDataInstance()
									.incrementPacketSent(nodeListIndex);

							if ((flit.getSource() >> 3) == (flit.getDest() >> 3)) {
								NetworkManager_3D.getStatDataInstance().sameUnit++;
							}
						}
						if (curCycle > IConstants_3D.WARM_UP_CYCLE)
							System.out.println("Sent: from" + this.address
									+ " to: " + flit.getDest() + " cycle: "
									+ curCycle + " length: "
									+ flit.getPacketLength() + " gentime: "
									+ flit.getGenTimeStamp());

					}
					if (packet.size() == 0) {
						messageList[i] = null;
						messageVCIndex[i] = -1;
						messageCount--;
					} else {
						packet = (Vector) messageList[i];
					}
					break; 
				}
			}
		}
	}


	private void forwardFlitToSwitch(int curCycle) {
		int count = 0;
		Flit_3D flit;


		lastOutVCServed = (int) (++lastOutVCServed % vcCount);
		while (count < vcCount) {
			if (outVCUsedList[lastOutVCServed] > 0
					&& outputBuffer.hasFlitToSend(lastOutVCServed)
					&& NetworkManager_3D.getHelpingUtility()
							.getConvertedCycle(
									outputBuffer.getBufferData(lastOutVCServed)
											.getLastServiceTimeStamp(),
									clockRateFactor) < NetworkManager_3D
							.getHelpingUtility().getConvertedCycle(curCycle,
									clockRateFactor)
					&& lastUsedOwnOutCycle < NetworkManager_3D.getHelpingUtility()
							.getConvertedCycle(curCycle, clockRateFactor)) {

	

				if (outputBuffer.getBufferData(lastOutVCServed).getType() == IConstants_3D.HEADER_FLIT) {
					// header flit. So need a free VC
					if (parent.isVCFreeInSwitch(linkNo, lastOutVCServed)) {

						lastUsedOwnOutCycle = NetworkManager_3D
								.getHelpingUtility().getConvertedCycle(
										curCycle, clockRateFactor);

						flit = outputBuffer.removeBufferData(lastOutVCServed,
								curCycle);

						flit.setLastServiceTimeStamp(curCycle);
						/** ** Eliminated : Done in the InputVCBuffer Class *** */


						parent.addInputBufferData(linkNo, flit, curCycle);
						this.linkUsed = true;
						if (IConstants_3D.TRACE) {
							try {
								RandomAccessFile raf = new RandomAccessFile(
										IConstants_3D.TRACE_FILE, "rw");
								raf.seek(raf.length());
								raf.writeBytes("\nNode " + address
										+ " Cycle out " + lastUsedOwnOutCycle
										+ " Switch Cycle " + curCycle
										+ " Header Flit("
										+ flit.getSourceNode() + ","
										+ flit.getDestinationNode()
										+ ") is moving from Node " + address
										+ " OutBuffer VC index "
										+ lastOutVCServed + " to Switch "
										+ parent.getAddress()
										+ " (Link,VC)) = " + linkNo + ","
										+ lastOutVCServed + ")");

								raf.close();
							} catch (IOException ioex) {
							}
						}

						break;
					}
					// else blocked. try next time
				} else {
					// data flit. need a free slot in VC buffer
					if (parent.hasFreeSlotInVCBuffer(linkNo, lastOutVCServed)) {

						lastUsedOwnOutCycle = NetworkManager_3D
								.getHelpingUtility().getConvertedCycle(
										curCycle, clockRateFactor);

						flit = outputBuffer.removeBufferData(lastOutVCServed,
								curCycle);

						flit.setLastServiceTimeStamp(curCycle);
		

						parent.addInputBufferData(linkNo, flit, curCycle);
						this.linkUsed = true;

						if (IConstants_3D.TRACE) {
							try {
								RandomAccessFile raf = new RandomAccessFile(
										IConstants_3D.TRACE_FILE, "rw");
								raf.seek(raf.length());
								raf.writeBytes("\nNode " + address
										+ " Cycle out " + lastUsedOwnOutCycle
										+ " Switch Cycle " + curCycle
										+ " Data Flit is moving from Node "
										+ address + " OutBuffer VC index "
										+ lastOutVCServed + " to Switch "
										+ parent.getAddress()
										+ " (Link,VC)) = " + linkNo + ","
										+ lastOutVCServed + ")");

								raf.close();
							} catch (IOException ioex) {
							}
						}

						break;
					}
				}
			}
			lastOutVCServed = (int) (++lastOutVCServed % vcCount);
			count++;
			// otherwise try for the next VC to send
		}
	}


	public boolean isInputVCFree(int vcId) {
		return inputBuffer.isVCFree(vcId);
	}


	public boolean hasFreeSlotInInputVC(int vcId) {
		return inputBuffer.hasFreeSlotInVC(vcId);
	}

	
	public boolean addInputBufferData(Flit_3D flit, int curCycle) {
		
	
		if (getAddress() != flit.getDest()) {
			
		
			
			System.err.println("WRONG: Node Address" + this.address
					+ " FlitSrc: " + flit.getSource() + " FlitDest: "
					+ flit.getDest() + " Type:" + flit.getType());
		}

		if (IConstants_3D.TRACE) {
			try {
				RandomAccessFile raf = new RandomAccessFile(
						IConstants_3D.TRACE_FILE, "rw");
				raf.seek(raf.length());
				if (IConstants_3D.HEADER_FLIT == flit.getType()) {
					raf.writeBytes("\nNode " + address + " Cycle In "
							+ lastUsedOwnInCycle + " Switch Cycle " + curCycle
							+ " ( " + flit.getSource() + "," + flit.getDest()
							+ ") " + " Header Flit(" + flit.getSourceNode()
							+ "," + flit.getDestinationNode()
							+ ") is received from Switch "
							+ parent.getAddress() + " at Node " + address
							+ " VC index " + flit.getVirtualChannelNo());
				} else {
					raf.writeBytes("\nNode " + address + " Cycle In "
							+ lastUsedOwnInCycle + " Switch Cycle " + curCycle
							+ " ( " + flit.getSource() + "," + flit.getDest()
							+ ") " + " Data Flit is received from Switch "
							+ parent.getAddress() + " at Node " + address
							+ " VC index " + flit.getVirtualChannelNo());

				}
				raf.close();
			} catch (IOException ioex) {
			}
		}

		if (curCycle > IConstants_3D.WARM_UP_CYCLE) {
			NetworkManager_3D.getStatDataInstance().incrementFlitReceived(
					nodeListIndex);
		}
		return inputBuffer.addBufferData(flit, flit.getVirtualChannelNo(),
				curCycle);

	}

	
	public void forwardFlitToNodeMessageCenter(int curCycle) {
		int count = 0;
		Vector packet;
		Flit_3D flit;

		lastInVCServed = (int) (++lastInVCServed % vcCount);
		while (count < vcCount) {
			if (null != inputBuffer.getBufferData(lastInVCServed)
					&& inputBuffer.hasFlitToSend(lastInVCServed)
					&& inputBuffer.getBufferData(lastInVCServed)
							.getLastServiceTimeStamp() < curCycle) {
				
				if (inputBuffer.getBufferData(lastInVCServed).getType() == IConstants_3D.HEADER_FLIT) {
					
					flit = inputBuffer.removeBufferData(lastInVCServed,
							curCycle);
					flit.setLastServiceTimeStamp(curCycle);

					packet = new Vector();
					packet.add(flit);
					receiveMessageList[flit.getVirtualChannelNo()] = packet;
					nodeReceivedFlitCounter[flit.getVirtualChannelNo()] = flit
							.getPacketLength() - 1;

					if (curCycle > IConstants_3D.WARM_UP_CYCLE)
						System.out.println("Receive Header: Dest: " + address
								+ " Src: " + flit.getSource() + " cycle: "
								+ curCycle);

					if (IConstants_3D.TRACE) {
						try {
							RandomAccessFile raf = new RandomAccessFile(
									IConstants_3D.TRACE_FILE, "rw");
							raf.seek(raf.length());
							raf
									.writeBytes("\nCycle "
											+ curCycle
											+ " ( "
											+ flit.getSource()
											+ ","
											+ flit.getDest()
											+ ") "
											+ " Header Flit("
											+ flit.getSourceNode()
											+ ","
											+ flit.getDestinationNode()
											+ ") is received at Message Center from Node Input Buffer at Node "
											+ address + " VC index "
											+ lastInVCServed);

							raf.close();
						} catch (IOException ioex) {
						}
					}

					break;

				} else {
				
					flit = inputBuffer.removeBufferData(lastInVCServed,
							curCycle);
					flit.setLastServiceTimeStamp(curCycle);

					packet = receiveMessageList[flit.getVirtualChannelNo()];

					if (IConstants_3D.TRACE) {
						try {
							RandomAccessFile raf = new RandomAccessFile(
									IConstants_3D.TRACE_FILE, "rw");
							raf.seek(raf.length());
							raf
									.writeBytes("\nCycle "
											+ curCycle
											+ " ( "
											+ flit.getSource()
											+ ","
											+ flit.getDest()
											+ ") "
											+ " Data Flit is received at Message Center from Node Input Buffer at Node "
											+ address + " VC index "
											+ lastInVCServed);

							raf.close();
						} catch (IOException ioex) {
						}
					}

				

					if (null != packet) {
						packet.add(flit);
					} else {
						System.err.println("Packet not found");
					}

					nodeReceivedFlitCounter[flit.getVirtualChannelNo()]--;

					if (nodeReceivedFlitCounter[flit.getVirtualChannelNo()] == 0) {
						if (curCycle > IConstants_3D.WARM_UP_CYCLE) {

							if (curCycle > IConstants_3D.WARM_UP_CYCLE)
								System.out.println("Receive Completed: Dest: "
										+ address + " Src: " + flit.getSource()
										+ " cycle: " + curCycle + " hop: "
										+ flit.getHopCount() + " genTime: "
										+ flit.getGenTimeStamp() + " time: "
										+ (curCycle - flit.getGenTimeStamp()));

							NetworkManager_3D.getStatDataInstance()
									.incrementPacketDelay(nodeListIndex,
											curCycle - flit.getGenTimeStamp());
							NetworkManager_3D.getStatDataInstance()
									.incrementPacketHopCount(nodeListIndex,
											flit.getHopCount());

					

						}
						receiveMessageList[flit.getVirtualChannelNo()] = null;
						dumpMessage(packet);
					}
					break;
				}
			}
			lastInVCServed = (int) (++lastInVCServed % vcCount);
			count++;
			
		}

	}

	private void dumpMessage(Vector packet) {
		int count = 1, prevHop = 0;
		Flit_3D flit;

		if (packet.isEmpty() == false) {
			flit = (Flit_3D) packet.firstElement();
			prevHop = flit.getHopCount();
		
		}
		while (packet.isEmpty() == false) {
			flit = (Flit_3D) packet.remove(0);
			if (prevHop != flit.getHopCount()) {
				System.out.println("Hop Mismatch " + prevHop + " , "
						+ flit.getHopCount());
			
			}
			count++;
		}
	}

	public void updateStatusAfterCycle(int curCycle) {
		inputBuffer.updateStatusAfterCycle();
		outputBuffer.updateStatusAfterCycle();

		// stat
		if (this.linkUsed == true) {
			this.linkUsed = false;
			if (curCycle > IConstants_3D.WARM_UP_CYCLE) {
				NetworkManager_3D.getStatDataInstance().incrementNodeLinkUse(
						nodeListIndex);
			}
		}
		if (curCycle > IConstants_3D.WARM_UP_CYCLE) {
			NetworkManager_3D.getStatDataInstance().incrementNodeInputBufferUse(
					nodeListIndex, inputBuffer.getNumSlotUsed());
			NetworkManager_3D.getStatDataInstance().incrementNodeOutputBufferUse(
					nodeListIndex, outputBuffer.getNumSlotUsed());
		}
	}

	
	public void setNodeListIndex(int index) {
		this.nodeListIndex = index;
	}

	
	public int getNodeListIndex() {
		return this.nodeListIndex;
	}

}