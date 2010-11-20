package project;

public class TrueAndEstimatedPos<A, B>
{
  public A truePos;
  public B estimatedPos;
	  
  public TrueAndEstimatedPos(A truePos, B estimatedPos)
  { 
    this.truePos = truePos;
    this.estimatedPos = estimatedPos;   
  }
  
  public void setTruePos(A truePos)
  {
	  this.truePos = truePos;
  }
  
  public void setEstimatedPos(B estimatedPos)
  {
	  this.estimatedPos = estimatedPos;
  }
  
  public A getTruePos()
  {
	  return truePos;
  }
  
  public B getEstimatedPos()
  {
	  return estimatedPos;
  }
  
  public String toString()
  { 
    return "(" + truePos.toString() + ", " + estimatedPos.toString() + ")"; 
  }

}