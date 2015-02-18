package org.manwhore.displayer.filter;

import java.text.ParseException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import org.manwhore.displayer.R;


/**
 * Class for handling relative dates.
 * 
 * @author siglerv
 *
 */
public class RelativeDate {
	
	private final String dayString;
	private final String weekString;
	private final String monthString;
	private final String yearString;
	
	private final String periodStart;
	private final String periodEnd;
	
	private final Calendar calendar;

	public RelativeDate(String relDate, Context context) throws Exception
	{
				
		Resources res = context.getResources();
		this.dayString = res.getString(R.string.rd_day);
		this.monthString = res.getString(R.string.rd_month);
		this.yearString = res.getString(R.string.rd_year);
		this.weekString = res.getString(R.string.rd_week);
		
		this.periodStart = res.getString(R.string.rd_start);
		this.periodEnd = res.getString(R.string.rd_end);

		this.calendar = parseDate(relDate);
	}
	
	/**
	 * Returns date this object represents in milliseconds.
	 * 
	 * @return
	 */
	public long getTimeInMillis()
	{
		return this.calendar.getTimeInMillis();
	}
	
	protected Calendar parseDate(String relDate) throws Exception
	{
		Calendar base = Calendar.getInstance();
		base.setFirstDayOfWeek(Calendar.MONDAY);
		
		List<Token> tokens = parseForTokens(relDate);
				
		int step = 0; //0 - modifier or number, 1 - NUMBER, 2 - unit - start/end, 3 - modifier2, 4 number2, 5unit2
		
		Integer parsedNumber = 1;
		while (!tokens.isEmpty())
		{
			Token t = tokens.remove(0);
			
			switch (step)
			{
				case 0: 
				{
					if (t.getType().equals(Token.Type.MODIFIER))	
					{
						if (t.getValue().equals("-"))
						parsedNumber *= -1;
						step = 1;
						break;
					}					
					else if (t.getType().equals(Token.Type.NUMBER))
					{
						//pass through to next step
						step = 2;
					}
					else
						throw new Exception("Unexpected token: " + t.getType().toString() +" - '" + t.getValue() + "', step: " + step);
				}
				case 1: 
				{
					if (t.getType().equals(Token.Type.NUMBER))
					{
						parsedNumber *= Integer.parseInt(t.getValue());												
					}
					else
						throw new Exception("Unexpected token: " + t.getType().toString() +" - '" + t.getValue() + "', step: " + step);
					
					step = 2;
					break;
				}
				case 2:
				{
					Token nextToken = null;
					if (tokens.size() != 0)
						nextToken = tokens.remove(0);
					
					if (t.getType().equals(Token.Type.UNIT))
					{
						if ( (nextToken != null) && Token.Type.PERIOD.equals(nextToken.getType()))
						{
							base = getBase(t.getValue(), nextToken.getValue(), parsedNumber);
							parsedNumber = 1;
							step = 3;
							break;
						}
						else 
						{
							addUnit(t.getValue(), parsedNumber, base);
							parsedNumber = 1;
							step = 3;
							
							if (nextToken == null) // if there's a next token, fall over to step 3
							{								
								break;
							}
							else
							{
								t = nextToken;
							}
						
							
						}						
					}
					else
						throw new Exception("Unexpected token: " + t.getType().toString() +" - '" + t.getValue() + "', step: " + step);										
				}
				case 3:
				{
					if (t.getType().equals(Token.Type.MODIFIER))	
					{
						if (t.getValue().equals("-"))
						parsedNumber *= -1;					
					}
					else
						throw new Exception("Unexpected token: " + t.getType().toString() +" - '" + t.getValue() + "', step: " + step);
					
					step = 4;
					break;
				}
				case 4:
				{
					if (t.getType().equals(Token.Type.NUMBER))
					{
						parsedNumber *= Integer.parseInt(t.getValue());												
					}
					else
						throw new Exception("Unexpected token: " + t.getType().toString() +" - '" + t.getValue() + "', step: " + step);
					
					step = 5;
					break;
				}
				case 5:
				{
									
					if (t.getType().equals(Token.Type.UNIT))
					{
						addUnit(t.getValue(), parsedNumber, base);
						parsedNumber = 1;
					}
					else
						throw new Exception("Unexpected token: " + t.getType().toString() +" - '" + t.getValue() + "', step: " + step);
					
					step = 3;
					break;
				}
			}			
		}						
		
		return base;
	}
	
	protected void addUnit(String unit, Integer multiplier, Calendar calendar)
	{
		if (this.dayString.equals(unit))
		{
			calendar.roll(Calendar.DAY_OF_MONTH, multiplier);
		}
		if (this.weekString.equals(unit))
		{
			calendar.roll(Calendar.WEEK_OF_YEAR, multiplier);
		}
		else if (this.monthString.equals(unit))
		{
			calendar.roll(Calendar.MONTH, multiplier);
		}
		else if (this.yearString.equals(unit))
		{
			calendar.roll(Calendar.YEAR, multiplier);
		}
	}
	
	protected Calendar getBase(String unit, String period, Integer multiplier)
	{
		Calendar result = Calendar.getInstance();
				
		if (this.periodEnd.equals(period))
		{
			multiplier += 1;
		}
		
		result.set(Calendar.MILLISECOND, 0);
		result.set(Calendar.SECOND, 0);
		result.set(Calendar.MINUTE, 0);
		result.set(Calendar.HOUR_OF_DAY, 0);
		
		if (this.dayString.equals(unit))
		{
			result.add(Calendar.DAY_OF_MONTH, multiplier);
		}
		else if (this.weekString.equals(unit))
		{
			result.set(Calendar.DAY_OF_WEEK, result.getFirstDayOfWeek());
			result.add(Calendar.WEEK_OF_YEAR, multiplier);
		}
		else if (this.monthString.equals(unit))
		{
			result.set(Calendar.DAY_OF_MONTH, 1);
			result.add(Calendar.MONTH, multiplier);
		}
		else if (this.yearString.equals(unit))
		{
			result.set(Calendar.DAY_OF_MONTH, 1);
			result.set(Calendar.MONTH, 1);
			result.add(Calendar.YEAR, multiplier);
		}
		
		return result;
	}
	
	
	protected List<Token> parseForTokens(String string) throws ParseException
	{
		List<Token> result = new LinkedList<Token>();
		
		string = string.replaceAll(" ", ""); //strip all spaces
		
		int strLen = string.length();
		
		Token parsedToken = null;
		String units = this.dayString + this.weekString + this.monthString + this.yearString;
		for (int i = 0; i < strLen; i++)
		{
			Character c = Character.valueOf(string.charAt(i));
			Token.Type tType = null;
			
			if (c.equals('-') || c.equals('+'))
				tType = Token.Type.MODIFIER;
			else if (Character.isDigit(c))
				tType = Token.Type.NUMBER;
			else if (c.equals(this.periodStart.charAt(0)) || c.equals(this.periodEnd.charAt(0)))
				tType = Token.Type.PERIOD;			
			else if (units.indexOf(c) != -1)
				tType = Token.Type.UNIT;
			else 
				throw new ParseException("Unknown token: " + c, i); 
			
			if (parsedToken == null)
				parsedToken = new Token(tType, c.toString());
			else if (parsedToken.getType().equals(Token.Type.NUMBER) && tType.equals(Token.Type.NUMBER))
				parsedToken.setValue(parsedToken.getValue() + c);
			else
			{
				result.add(parsedToken);
				parsedToken = new Token(tType, c.toString());
			}			
		}
		if (parsedToken != null)
			result.add(parsedToken);
		
		return result;		
	}
	
}
