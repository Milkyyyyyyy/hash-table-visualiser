package util;

public class NodeLayout{
    public float x, y;
    public float targetX, targetY;
    public boolean isActive = false;

    public NodeLayout(float x, float y){
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
    }
}
