package net.sf.okapi.filters.openxml;

/**
 * String mmYYnn for sheet mm, column YY, row nn for one cell in an Excel workbook 
 * 
 */
public class Excell
{
	String sheet;
	String column;
	String row;
	String sNumnum = "0123456789";
	String sLtr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public Excell(String sCell)
	{
		setCell(sCell);
	}
	public void setCell(String sCell)
	{
		int len,ndx=0,ndx2=0;
		if (sCell==null)
			stemCell();
		len = sCell.length();
		if (len<=0)
			stemCell();
		for(ndx=0;ndx<len;ndx++)
		{
			if (sNumnum.indexOf(sCell.charAt(ndx)) == -1) // not a number
				break;
		}
		if (ndx>0)
			sheet = sCell.substring(0,ndx);
		else
			sheet = "1";
		for(ndx2=ndx;ndx2<len;ndx2++)
		{
			if (sLtr.indexOf(sCell.charAt(ndx2)) == -1) // not a number
				break;
		}
		if (ndx2>ndx)
			column = sCell.substring(ndx,ndx2);
		else
			column = "A";
		for(ndx=ndx2;ndx<len;ndx++)
		{
			if (sNumnum.indexOf(sCell.charAt(ndx)) == -1) // not a number
				break;
		}
		if (ndx>ndx2)
			row = sCell.substring(ndx2,ndx);
		else
			row = "1";	
	}
	private void stemCell()
	{
		sheet = "1";
		column = "A";
		row = "1";		
	}
	public String getCell()
	{
		return sheet+column+row;
	}
	public void setColumn(String column)
	{
		this.column = column; // should do error checking that this could be an Excel column
	}
	public String getColumn()
	{
		return column;
	}
	public void setRow(String row)
	{
		this.row = row; // should do error checking that this is all numbers
	}
	public String getRow()
	{
		return row;
	}
	public void setSheet(String sheet)
	{
		this.sheet = sheet; // should do error checking that this is all numbers
	}
	public String getSheet()
	{
		return sheet;
	}
}