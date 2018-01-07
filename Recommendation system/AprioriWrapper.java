public class AprioriWrapper {
	
	 Integer hashValue;
     Integer count;
    
    public AprioriWrapper(Integer hashValue, Integer count) {
       this.hashValue = hashValue;
       this.count = count;
    }
    

    public Integer getHashValue() { return this.hashValue; }
    public Integer getCount() { return this.count; }

    
}