package bluebox.scr.data;

import java.io.File;

public class Book {
	public String name;
	public String path;
	
	public Book()
	{
	}
	
	public Book(File f)
	{
		name = f.getName();
		path = f.getPath();
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
