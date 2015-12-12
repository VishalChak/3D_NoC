import java.util.*;


public class Network_3D {
	
	
	private Vector nodeList;

	
	private Vector switchList;
	

	
	public Network_3D(int networkType) {
		nodeList = new Vector();
		switchList = new Vector();

			createMeshNetwork();
			setAdjacentMeshSwitch();
			
			
			
		


	}
	
	


	
	
	
	
	public void createMeshNetwork() {
		int i, address,x_axis, y_axis, z_axis, noOfAdjNode;
		double factor;
		MeshSwitch_3D meshSwitch;
		Node_3D nd;

		noOfAdjNode = IConstants_3D.MESH_ADJ_NODE;
		
		IConstants_3D.MESH_ROW = (int) Math.floor(Math.sqrt(64));
		
		IConstants_3D.MESH_COL = (int) Math.ceil(Math.sqrt(64));
		IConstants_3D.MESH_NODE_BITS_REQ = (int) Math.ceil(Math
				.log(IConstants_3D.MESH_ADJ_NODE)
				/ Math.log(2));
		IConstants_3D.CURRENT_LINK_COUNT = IConstants_3D.MESH_ADJ_NODE + 6;
		IConstants_3D.MESH_ROW_BITS = (int) Math.ceil(Math
				.log(IConstants_3D.MESH_ROW)
				/ Math.log(2));
		IConstants_3D.Z_AXIS_BITS=(int)Math.ceil(Math
				.log(IConstants_3D.NUMBER_OF_SWITCH/64)/Math.log(2));
		IConstants_3D.MESH_COL_BITS = (int) Math.ceil(Math
				.log(IConstants_3D.MESH_COL)
				/ Math.log(2));
		
		
		
		IConstants_3D.NUMBER_OF_SWITCH=IConstants_3D.NUMBER_OF_IP_NODE;
			
		
		

		// Creates the Mesh Switches
		for (i = 0; i < IConstants_3D.NUMBER_OF_SWITCH; i++) {
			x_axis = (i / 8)%8;
			y_axis = i % 8;
			z_axis = i / 64;
			//address = (x_axis << IConstants_3D.MESH_COL_BITS) + y_axis;
			address =i;
			meshSwitch = new MeshSwitch_3D(IConstants_3D.CURRENT_LINK_COUNT,
					IConstants_3D.CURRENT_VC_COUNT, address, noOfAdjNode,
					IConstants_3D.CURRENT_ADJ_SWITCH, i);
			switchList.add(meshSwitch);
      
			address = address << IConstants_3D.MESH_NODE_BITS_REQ;
			
			
			for (int k = 0; k < noOfAdjNode; k++) {
				factor = NetworkManager_3D.getHelpingUtility()
						.getNextRandomNumber();
				factor = factor * 5 + 6;
				int intVal = (int) factor;
				factor = (double) intVal / 10;

				if (IConstants_3D.ASYNCHRONOUS)
				{
					
					nd = new Node_3D(address + k, meshSwitch, k,IConstants_3D.CURRENT_VC_COUNT, factor);
				}
				else
					{
				
					nd = new Node_3D(address + k, meshSwitch, k,IConstants_3D.CURRENT_VC_COUNT, 1.0);
					}
				
				meshSwitch.setAdjacentNode(nd, k);
				nodeList.add(nd);
				
			}
		}
		
		for (i = 0; i < IConstants_3D.NUMBER_OF_IP_NODE; i++) {
			((Node_3D) nodeList.get(i)).setNodeListIndex(i);
		}
	}

	
	
	private void setAdjacentMeshSwitch() {
		MeshSwitch_3D meshSwitch;
		int noOfAdjNode;
		int i, x_axis, y_axis,z_axis ;

		

		int numSwitch = IConstants_3D.NUMBER_OF_SWITCH;
         
		for (i = 0; i < numSwitch; i++)
		
		{
			x_axis = (i / 8)%8;
			y_axis = i % 8;
			z_axis = i / 64;
			meshSwitch = (MeshSwitch_3D) (switchList.get(i));
			noOfAdjNode = IConstants_3D.MESH_ADJ_NODE;
			if (x_axis > 0) {
				meshSwitch.setAdjacentSwitch((MeshSwitch_3D) switchList
						.get(i-8),
						IConstants_3D.SWITCH_TOP);
			} else {
				meshSwitch.setInputLinkController(IConstants_3D.SWITCH_TOP
						+ noOfAdjNode, null);
				meshSwitch.setOutputLinkController(IConstants_3D.SWITCH_TOP
						+ noOfAdjNode, null);
			}
			if (x_axis < 7) {
				meshSwitch.setAdjacentSwitch((MeshSwitch_3D) switchList.get(i+8),
						IConstants_3D.SWITCH_BOTTOM);
			} else {
				meshSwitch.setInputLinkController(IConstants_3D.SWITCH_BOTTOM
						+ noOfAdjNode, null);
				meshSwitch.setOutputLinkController(IConstants_3D.SWITCH_BOTTOM
						+ noOfAdjNode, null);
				}
			if (y_axis > 0) {
				meshSwitch.setAdjacentSwitch((MeshSwitch_3D) switchList
						.get(i-1),
						IConstants_3D.SWITCH_LEFT);
			} else {
				meshSwitch.setInputLinkController(IConstants_3D.SWITCH_LEFT
						+ noOfAdjNode, null);
				meshSwitch.setOutputLinkController(IConstants_3D.SWITCH_LEFT
						+ noOfAdjNode, null);
				}
			if (y_axis < 7) {
				meshSwitch.setAdjacentSwitch((MeshSwitch_3D) switchList
						.get(i+1),
						IConstants_3D.SWITCH_RIGHT);
			} else {
				meshSwitch.setInputLinkController(IConstants_3D.SWITCH_RIGHT
						+ noOfAdjNode, null);
				meshSwitch.setOutputLinkController(IConstants_3D.SWITCH_RIGHT
						+ noOfAdjNode, null);
			}
			if(z_axis>0)
				meshSwitch.setAdjacentSwitch((MeshSwitch_3D)
						switchList.get(i-64), IConstants_3D.SWITCH_UP);
			else
			{
				meshSwitch.setInputLinkController(IConstants_3D.SWITCH_UP+noOfAdjNode, null);
				meshSwitch.setOutputLinkController(IConstants_3D.SWITCH_UP+noOfAdjNode, null);
			}
			if(z_axis<(numSwitch/64)-1)
				meshSwitch.setAdjacentSwitch((MeshSwitch_3D)
						switchList.get(i+64), IConstants_3D.SWITCH_DOWN);
			else
			{
				meshSwitch.setInputLinkController(IConstants_3D.SWITCH_DOWN+noOfAdjNode, null);
				meshSwitch.setOutputLinkController(IConstants_3D.SWITCH_DOWN+noOfAdjNode, null);
			}
			
		}
	}

	public void setInitalEvents() {
		int i;
		Switch_3D nocSwitch;
		for (i = 0; i < nodeList.size(); i++) {
			Node_3D node = (Node_3D) nodeList.get(i);
			node.nodeTraffic.setNextMsgGenTime(0);
		}

		for (i = 0; i < switchList.size(); i++) {
			nocSwitch = (Switch_3D) switchList.get(i);
			nocSwitch.resetSwitchingInfoVector();
		}

		// track no of link active in each Switch. For statistical purpose
		for (i = 0; i < switchList.size(); i++) {
			nocSwitch = (Switch_3D) switchList.get(i);
			NetworkManager_3D.getStatDataInstance().setSwitchNumLink(i,
					nocSwitch.getNumLinkActive());
		}

	}

	

	public void moveNodeTrafficFromNodeToSwitch(int nCycle) {
		int i;
		Node_3D node;
		for (i = 0; i < nodeList.size(); i++) {
			node = (Node_3D) nodeList.get(i);
			node.updateOutput(nCycle);
		}
	}

	public void updateSwitchTrafficPathRequest(int nCycle) {
		int i;
		Switch_3D nocSwitch;
		for (i = 0; i < switchList.size(); i++) {
			nocSwitch = (Switch_3D) switchList.get(i);
			nocSwitch.updateSwitchOutPathRequest(nCycle);
		}
	}

	
	public void moveSwitchTrafficFromInputBufferToOutputBuffer(int nCycle) {
		int i;
		Switch_3D nocSwitch;
		for (i = 0; i < switchList.size(); i++) {
			nocSwitch = (Switch_3D) switchList.get(i);
			nocSwitch.moveInputBufferToOutputBuffer(nCycle);
		}
	}

	
	public void moveSwitchTrafficFromOutputBufferToInputBufferOfNodeSwitch(
			int nCycle) {
		int i;
		Switch_3D nocSwitch;
		for (i = 0; i < switchList.size(); i++) {
			nocSwitch = (Switch_3D) switchList.get(i);
			nocSwitch.moveSwitchOutputBufferToInputBufferOfNodeSwitch(nCycle);
		}
	}

	public void moveNodeTrafficFromInputBufferToNodeMsgCenter(int nCycle) {
		int i;
		Node_3D node;
		for (i = 0; i < nodeList.size(); i++) {
			node = (Node_3D) nodeList.get(i);
			node.forwardFlitToNodeMessageCenter(nCycle);
		}
	}


	public void updateAfterCycleStatus(int curCycle) {
		int i;
		Switch_3D nocSwitch;
		Node_3D node;
		for (i = 0; i < nodeList.size(); i++) {
			node = (Node_3D) nodeList.get(i);
			node.updateStatusAfterCycle(curCycle);
		}
		for (i = 0; i < switchList.size(); i++) {
			nocSwitch = (Switch_3D) switchList.get(i);
			nocSwitch.updateStatusAfterCycle(curCycle);
		}
	}

}