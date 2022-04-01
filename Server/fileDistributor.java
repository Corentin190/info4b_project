import java.util.*;

public class fileDistributor{
  private ArrayList<String> fileFolder;

  public fileDistributor(ArrayList<String> fileFolder){
    this.fileFolder = fileFolder;
  }

  public synchronized String popFirstFile(){
    if(this.fileFolder.size()>0){
      String res = this.fileFolder.get(0);
      this.fileFolder.remove(0);
      return res;
    }else return null;
  }
}
