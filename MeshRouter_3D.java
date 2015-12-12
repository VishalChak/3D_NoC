

public class MeshRouter_3D implements Router_3D {

	public int determineRoute(int source, int dest, int switchAddr) {
		
		return mesh8_static_route_X_Y_Z1(source, dest, switchAddr);
	}

	
	private int mesh8_static_route(int source, int dest, int switchAddr) {
		int destS = -1, destRow = -1, destCol = -1;
		int switchRow = -1, switchCol = -1;

		
		destS = dest >> IConstants_3D.MESH_NODE_BITS_REQ;
		destRow = destS >> IConstants_3D.MESH_COL_BITS;
		destCol = destS & ((1 << IConstants_3D.MESH_COL_BITS) - 1);

		switchRow = switchAddr >> IConstants_3D.MESH_COL_BITS;
		switchCol = switchAddr & ((1 << IConstants_3D.MESH_COL_BITS) - 1);

		if (destS == switchAddr) {
			

			return dest - (switchAddr << IConstants_3D.MESH_NODE_BITS_REQ);
		} else if (destRow == switchRow) {
			if (destCol < switchCol) {
				return IConstants_3D.SWITCH_LEFT + IConstants_3D.MESH_ADJ_NODE;
			} else if (destCol > switchCol) {
				return IConstants_3D.SWITCH_RIGHT + IConstants_3D.MESH_ADJ_NODE;
			}
		} else if (destRow < switchRow) {
			return IConstants_3D.SWITCH_TOP + IConstants_3D.MESH_ADJ_NODE;
		} else if (destRow > switchRow) {
			return IConstants_3D.SWITCH_BOTTOM + IConstants_3D.MESH_ADJ_NODE;
		}
		return -1;
	}
	
	
	private int mesh8_static_route1(int source, int dest, int switchAddr) {
		int destS = -1, destX = -1, destY = -1,destZ = -1;
		int switchX = -1, switchY = -1, switchZ = -1;
		
		destX = (dest/8)%8;
		destY = dest%8;
		destZ = dest/64;
		
		
		switchX = (switchAddr/8)%8;
		switchY = switchAddr%8;
		switchZ = switchAddr%64;
		if(destS == switchAddr)
			return dest - switchAddr;
		
		else if(destX==switchX){
			if(destY==switchY){
				if(destZ>switchZ)
					return IConstants_3D.SWITCH_DOWN;
				else if(destZ<switchZ)
					return IConstants_3D.SWITCH_UP;
			}
			else if(destY<switchY)
				return IConstants_3D.SWITCH_LEFT;
			else if (destY>switchY)
				return IConstants_3D.SWITCH_RIGHT;
		}
		else if(destX<switchY)
			return IConstants_3D.SWITCH_TOP;
		else if(destX>switchY)
			return IConstants_3D.SWITCH_BOTTOM;
		return -1;
		}

	
	private int mesh8_static_route_X_Y_Z(int source, int dest, int switchAddr) {
		int destS = -1, destX = -1, destY= -1,destZ;
		int switchX = -1, switchY = -1,switchZ=-1;

		
		destS = dest;
		destX=(dest/8)%8;
		destY = dest%8;
		destZ = dest/64;

	
		
		
		switchX = (switchAddr/8)%8;
		switchY = switchAddr%8;
		switchZ = switchAddr/64;
		
		 
		if (destS == switchAddr) {
			return 0;
			}
		else if (destX == switchX) {
			if(destY == switchY){
				
				if(destZ<switchZ)
				{
				
					return IConstants_3D.SWITCH_UP+ IConstants_3D.MESH_ADJ_NODE;
				}

				
			else if(destZ>switchZ)
				{
					
					return IConstants_3D.SWITCH_DOWN +IConstants_3D.MESH_ADJ_NODE;
				}
						}
			else if (destY < switchY) {
				
				return IConstants_3D.SWITCH_LEFT + IConstants_3D.MESH_ADJ_NODE;
				} 
			else if (destY > switchY) {
			
				return IConstants_3D.SWITCH_RIGHT + IConstants_3D.MESH_ADJ_NODE;
				}
			}
		
		else if (destX < switchX) {
			
			return IConstants_3D.SWITCH_TOP + IConstants_3D.MESH_ADJ_NODE;
		} 
		else if (destX > switchX) {
			
			return IConstants_3D.SWITCH_BOTTOM + IConstants_3D.MESH_ADJ_NODE;
		}
		return -1;
	}
	
	
	
	
	
	
	static	int mesh8_static_route_X_Y_Z1(int source, int dest, int switchAddr){
		int destS = -1, destX = -1, destY= -1,destZ;
		int switchX = -1, switchY = -1,switchZ=-1;
		destS = dest;
		destX=(dest/8)%8;
		destY = dest%8;
		destZ = dest/64;
		
		switchX = (switchAddr/8)%8;
		switchY = switchAddr%8;
		switchZ = switchAddr/64;
		
		if(destS == switchAddr) {
			return 0;
			}
		else if(destZ==switchZ){
			if(destX==switchX){
				if(destY>switchY)
					return IConstants_3D.SWITCH_RIGHT +IConstants_3D.MESH_ADJ_NODE;
				else if(destY<switchY)
					return IConstants_3D.SWITCH_LEFT+IConstants_3D.MESH_ADJ_NODE;
				}
			else if(destX>switchX)
				return IConstants_3D.SWITCH_BOTTOM +IConstants_3D.MESH_ADJ_NODE;
			else if(destX<switchX)
				return IConstants_3D.SWITCH_TOP +IConstants_3D.MESH_ADJ_NODE;
			return switchAddr-8;
				}
		else if(destZ>switchZ){
			if(switchY%2==0)
				return IConstants_3D.SWITCH_RIGHT +IConstants_3D.MESH_ADJ_NODE;
			else
				return IConstants_3D.SWITCH_DOWN+IConstants_3D.MESH_ADJ_NODE;
			}
		else if(destZ<switchZ){
			if(switchY%2==0)
				return IConstants_3D.SWITCH_RIGHT +IConstants_3D.MESH_ADJ_NODE;
			else
				return IConstants_3D.SWITCH_UP+IConstants_3D.MESH_ADJ_NODE;
				}
		return -1;
		}

}