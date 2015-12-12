import java.util.*;
public class New_Routing_X_Y_Z {
	static Scanner scan=new Scanner (System.in);
	
	private static int mesh8_static_route_X_Y_Z(int source, int dest, int switchAddr) {
		int destS = -1, destX = -1, destY= -1,destZ;
		int switchX = -1, switchY = -1,switchZ=-1;

		

		//destS = dest >> IConstants_3D.MESH_NODE_BITS_REQ;
		destS = dest;
		destX=(dest/8)%8;
		destY = dest%8;
		destZ = dest/64;

	
		
		//switchY = switchAddr & ((1 << IConstants_3D.MESH_COL_BITS) - 1);
		switchX = (switchAddr/8)%8;
		switchY = switchAddr%8;
		switchZ = switchAddr/64;
		
		 
		if (destS == switchAddr) {
			return 0;
			}
		else if (destX == switchX) {
			if(destY == switchY){
				
				if(destZ>switchZ)
				{
					
					return switchAddr+64;
				}
				else if(destZ<switchZ)
				{
					
					return switchAddr-64;
				}
			}
			else if (destY < switchY) {
				return switchAddr-1;
				} 
			else if (destY > switchY) {
				return switchAddr+1;
				}
			}
		
		else if (destX < switchX) {
			return switchAddr-8;
		} 
		else if (destX > switchX) {
			return switchAddr+8;
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
	
	
	if (destS == switchAddr) {
		return 0;
		}
	else if(destZ==switchZ){
		if(destX==switchX){
			if(destY>switchY)
				return switchAddr+1;
			else if(destY<switchY)
				return switchAddr-1;
		}
		else if(destX>switchX)
			return switchAddr+8;
		else if(destX<switchX)
			return switchAddr-8;
	}
	
	else if(destZ>switchZ){
		if(switchY%2==0)
			return switchAddr +1;
		else
			return switchAddr+64;
	}
	
	
	else if(destZ<switchZ){
		if(switchY%2==0)
			return switchAddr +1;
		else
			return switchAddr-64;

   }
	
	return -1;
}
	
	
	
	
	
	public static void main(String [] args){
		int s=-1,d=-1,sw=-1,count=0;
		s=scan.nextInt();
		d=scan.nextInt();
		sw=s;
		
		while(sw!=d){
			sw=mesh8_static_route_X_Y_Z1(s,d,sw);
			count++;
			if(sw==d)
				System.out.println("Destination found at  "+sw+",  Hop Count="+count);
			else
				System.out.println("next Switch is "+sw);
		}
	}

}
