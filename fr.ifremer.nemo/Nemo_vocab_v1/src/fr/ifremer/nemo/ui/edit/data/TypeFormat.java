package fr.ifremer.nemo.ui.edit.data;


public class TypeFormat extends Object
{

    private String format, example;

    /**
     * Getter sur format.
     * 
     * @return the format
     */
    public String getFormat()
    {
        return format;
    }

    /**
     * Setter sur format.
     * 
     * @param format
     *            the format to set
     */
    public void setFormat(String format)
    {
        this.format = format;
    }

    /**
     * Getter sur example.
     * 
     * @return the example
     */
    public String getExample()
    {
        return example;
    }

    /**
     * Setter sur example.
     * 
     * @param example
     *            the example to set
     */
    public void setExample(String example)
    {
        this.example = example;
    }

    public TypeFormat(String format, String example)
    {
        this.setFormat(format);
        this.setExample(example);
    }

}
