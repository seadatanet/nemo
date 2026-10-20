package fr.ifremer.nemo.output.netCDF;

import java.util.Comparator;

public class ArrayIndexComparator implements Comparator<Integer>
{
    private final Double[] array;

    public ArrayIndexComparator(Double[] array)
    {
        this.array = array;
    }

    public Integer[] createIndexArray()
    {
        Integer[] indexes = new Integer[array.length];
        for (int i = 0; i < array.length; i++)
        {
            indexes[i] = i; // Autoboxing
        }
        return indexes;
    }

//    @Override
    public int compare(Integer index1, Integer index2)
    {
         // Autounbox from Integer to int to use as array indexes
    	Double first=Double.NaN;
    	Double second=Double.NaN;
    	if (array[index1]!=null){
    		first=array[index1];
    	}
    	if (array[index2]!=null){
    		second=array[index2];
    	}
        return first.compareTo(second);
    }
}
