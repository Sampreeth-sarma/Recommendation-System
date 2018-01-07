import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Apriori {
		
	public static void main(String[] args) throws FileNotFoundException, IOException,SecurityException {
		
		LinkedHashMap<List<Integer>, Integer> solution = new LinkedHashMap<List<Integer>, Integer>();
		HashSet<LinkedHashMap<List<Integer>, Integer>> frequentItemLists = new HashSet<LinkedHashMap<List<Integer>, Integer>>();
		HashMap<List<Integer>, HashSet<Integer>> itemOcurrences = new HashMap<>();
		
		//program begins
		
		int numOfTrans=0;
		
		//to ceate new file that contain integer items
		FileWriter f= new FileWriter("transactionsDBNew.txt");
		PrintWriter pw = new PrintWriter(f);
		
		
		//to keep track of the item mapping and the individual count of items
		LinkedHashMap<String,AprioriWrapper> hashMap=new LinkedHashMap<String,AprioriWrapper>(); 
		
		// to read the input file
		Path path = Paths.get("transactionDB.txt");
		List<String> lines=Files.readAllLines(path);
		
		@SuppressWarnings("resource")
		Scanner sc= new Scanner(System.in);
		System.out.println("enter the minimum support count and k");
		int min_sup =  sc.nextInt();
		int k_value = sc.nextInt();	
		
		FileWriter f2= new FileWriter("out_s="+min_sup+"_k="+k_value+"+.txt");
		
		
		long start = System.nanoTime();
		for(String line : lines)
		{
			numOfTrans++;
			String[] items = line.split(" ");
			ArrayList<Integer> arr = new ArrayList<Integer>();
			for(String item : items)
			{
				if(!hashMap.containsKey(item))
					hashMap.put(item, new AprioriWrapper(new Integer(hashMap.size()+1),new Integer(0)));
				
				int hashValue = hashMap.get(item).getHashValue();
				hashMap.put(item, new AprioriWrapper(new Integer(hashValue),new Integer(hashMap.get(item).getCount()+1)));
				
				pw.print(hashValue+" ");
				arr.add(hashValue);
				List<Integer> list = new ArrayList<>();
				list.add(hashValue);
				
				if (itemOcurrences.containsKey(list))
					itemOcurrences.get(list).add(numOfTrans);
				else
				{
					HashSet<Integer> temp = new HashSet<>();
					temp.add(numOfTrans);
					itemOcurrences.put(list, temp);
				}
				
			}
			pw.println();

		}
		
		PrintWriter pw2 = new PrintWriter(f2);


		
		Iterator<?> ite = hashMap.entrySet().iterator();	
		while( ite.hasNext()){

			Map.Entry keyValue = (Map.Entry)ite.next();
			AprioriWrapper val = (AprioriWrapper) keyValue.getValue();
			
			if(val.getCount() >= min_sup) {
					
				List<Integer> temp = new ArrayList<Integer>();
				temp.add(val.getHashValue());		
				solution.put(temp, val.getCount());
				if( k_value==1){
					
					
					ArrayList<String> outputMerge = getStringFromHash(hashMap,temp);
        			Collections.sort(outputMerge);
        			String str = outputMerge.toString();
        			str = str.substring(1,str.length()-1);
        			str.replaceAll("\\,", "");			        			
    				pw2.println(str +" ("+val.getCount()+")");
					
				}
				temp=null;
			}
			else{
				ite.remove();	
			}
				
		}
		
		int jghfd=0;
		while (true)
		{
			jghfd++;
			LinkedHashMap<List<Integer>, Integer> newSolution = new LinkedHashMap<>(solution);
			LinkedHashMap<List<Integer>, Integer> tempSolution = getFrequentItemMap(newSolution, min_sup, itemOcurrences,pw2,k_value,hashMap);
			
			if (tempSolution.size() <= 1)
				break;
			solution.clear();			
			solution.putAll(tempSolution);
			tempSolution.clear();
		}
		
		pw2.close();
		System.out.println((System.nanoTime()-start)/(long)(Math.pow(10, 9))+" seconds");
		
	}


	private static LinkedHashMap<List<Integer>, Integer> getFrequentItemMap( LinkedHashMap<List<Integer>, Integer> solution, int min_sup, HashMap<List<Integer>, HashSet<Integer>> itemOcurrences, PrintWriter pw2, int k_value, LinkedHashMap<String, AprioriWrapper> hashMap) throws IOException
	{
		pw2.flush();
		 List<List<Integer>> cand = new ArrayList<List<Integer>>(solution.keySet());
		 LinkedHashMap<List<Integer>, Integer> solution1 =  new LinkedHashMap<List<Integer>, Integer>();
		 List<Integer> item1,item2,merge;
		 for( int i=0;i<cand.size();i++){
	    	for(int j=i+1;j<cand.size();j++){

	    		 item1 = cand.get(i);
	    		 item2 = cand.get(j);
	    		 merge = new ArrayList<Integer>();
	    		
	    		if (item1.size() == 1 && item2.size() == 1)
	    		{
		    		HashSet<Integer> item1Set = itemOcurrences.get(item1);
		    		HashSet<Integer> item2Set = itemOcurrences.get(item2);
		    		
		    		HashSet<Integer> intersection = new HashSet<>(item1Set);
		    		intersection.retainAll(item2Set);
		    		
		    		if (intersection.size() >= min_sup)
		    		{
		    			merge.addAll(item1);
		    			merge.add(item2.get(item2.size() - 1));
		    			solution1.put(merge, intersection.size());
		    			if(merge.size()>=k_value){
			    			ArrayList<String> outputMerge = getStringFromHash(hashMap,merge);
			    			Collections.sort(outputMerge);String str = outputMerge.toString();
		        			str = str.substring(1,str.length()-1);
		        			str = str.replaceAll("\\,", "");			        			
		    				pw2.println(str +" ("+ intersection.size()+")");
		    			}
		    		}
	    		}
	    		
	    		else
	    		{
		    		String string1 = item1.stream().map(Object::toString).collect(Collectors.joining(" "));
		    		String string2 = item2.stream().map(Object::toString).collect(Collectors.joining(" "));
		    		
		    		int index1 = string1.lastIndexOf(' ');
		    		int index2 = string2.lastIndexOf(' ');
			    	String string3 = string1.substring(0, index1);
			    	String string4 = string2.substring(0, index2);
		    		
		    		if (string3.compareTo(string4) == 0)
		    		{
		    			HashSet<Integer> intersection = new HashSet<>(itemOcurrences.get(Arrays.asList(item1.get(0))));
		    			
		    			for( int l=0;l<item1.size();l++){
		    				intersection.retainAll(itemOcurrences.get(Arrays.asList(item1.get(l))));
		    				
		    			}

		    			intersection.retainAll(itemOcurrences.get(Arrays.asList(item2.get(item2.size() - 1))));

			    		if (intersection.size() >= min_sup)
			    		{

			    			merge.addAll(item1);
			    			merge.add(item2.get(item2.size() - 1));
			    			
			    			if(prune(merge,solution)){
				    			solution1.put(merge, intersection.size());
				    			if(merge.size()>=k_value){
				        			ArrayList<String> outputMerge = getStringFromHash(hashMap,merge);
				        			Collections.sort(outputMerge);
				        			String str = outputMerge.toString();
				        			str = str.substring(1,str.length()-1);
				        			str = str.replaceAll(",", "");			        			
				    				pw2.println(str +" ("+ intersection.size()+")");
				    				outputMerge.clear();
				    			}
			    			}
			    		}
		    		}
		    		else
		    			break;
	    		}
	    		
	    		}
	     }
		return solution1;
	}


	private static ArrayList<String> getStringFromHash(LinkedHashMap<String, AprioriWrapper> hashMap, List<Integer> merge) {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<>();
		for(int item :merge){
			Iterator<Entry<String, AprioriWrapper>> ite = hashMap.entrySet().iterator();
			while(ite.hasNext()){
				Entry<String, AprioriWrapper> nextEle = ite.next();
				if(nextEle.getValue().hashValue==item){
					list.add(nextEle.getKey());
				}
			}
		}
		return list;
	}


	private static boolean prune(List<Integer> merge, LinkedHashMap<List<Integer>, Integer> solution) {
		// TODO Auto-generated method stub
		List<Integer> temp = new ArrayList<>();
		int size= merge.size();
		int consant = (int) (Math.pow(2, size)-1);
		int mask = 1;
		for( int i=0;i<size;i++){
			
			String comb;
			if(i<size-1)
				comb = (Integer.toBinaryString((consant^mask)));
			else
				comb="0"+(Integer.toBinaryString((consant^mask)));
			
			for(int j=0;j<comb.length();j++){
				
				if(comb.charAt(j)=='1')
					temp.add(merge.get(j));		
			}
			if(!solution.containsKey(temp))
				return false;
			mask=mask<<1;
			temp.clear();
		}
		return true;
	}

	
}
