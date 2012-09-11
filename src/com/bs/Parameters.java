package com.bs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class Parameters {
	
	public Parameters( String title){
		
		Title = title;
	}
	
	public Parameters( String title, String[] par_names){
		Title = title;
		ParametersName = par_names;
	       parameter_default.put("SPOT", 100.0);
	       parameter_default.put("STRIKE", 110.0);
	       parameter_default.put("RATE", 0.08);
	       parameter_default.put("DIVIDEND", 0.04);
	       parameter_default.put("EXPIRY", 0.5);
	       parameter_default.put("VOLATILITY", 0.25);
	       parameter_default.put("BARRIER", 105.);
	       parameter_default.put("REBATE", 3.);
	       
	       parameter_short.put("SPOT", "SPOT");
	       parameter_short.put("STRIKE", "STR ");
	       parameter_short.put("RATE", "RATE");
	       parameter_short.put("DIVIDEND", "DIV ");
	       parameter_short.put("EXPIRY", "EXP ");
	       parameter_short.put("VOLATILITY", "VOL ");
	       parameter_short.put("BARRIER", "BAR ");
	       parameter_short.put("REBATE", "REB ");
	      
	}

	public Parameters( String title, String[] par_names, double[] defaults){
		Title = title;
		ParametersName = par_names;
		int cnt=0;
		for(String par : par_names){
			parameter_default.put(par, defaults[cnt]);
			cnt++;
		}

	       parameter_short.put("SPOT", "SPOT");
	       parameter_short.put("STRIKE", "STR ");
	       parameter_short.put("RATE", "RATE");
	       parameter_short.put("DIVIDEND", "DIV ");
	       parameter_short.put("EXPIRY", "EXP ");
	       parameter_short.put("VOLATILITY", "VOL ");
	       parameter_short.put("BARRIER", "BAR ");
	       parameter_short.put("REBATE", "REB ");
	      
	}
	public LinearLayout buildParametersLayout(final Context context){
		
		LinearLayout.LayoutParams layoutParamsLinearLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setPadding(20, 20, 20, 20);
		linearLayout.setLayoutParams(layoutParamsLinearLayout);

		int cornerRadius = 5;
		float[] outerR = new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius};
		RectF inset = new RectF(4, 4, 4, 4);
		float[] innerR = new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius};
		ShapeDrawable circle = new ShapeDrawable(new RoundRectShape(outerR, inset, innerR));
		circle.setPadding(6, 0, 6, 0);

		circle.getPaint().setColor(Color.rgb(33, 90, 234));
		//circle.getPaint().setColor(Color.BLUE);
		linearLayout.setBackgroundDrawable(circle); 

		LinearLayout left_layout = new LinearLayout(context);
		LinearLayout right_layout = new LinearLayout(context);
		left_layout.setOrientation(LinearLayout.VERTICAL);
		right_layout.setOrientation(LinearLayout.VERTICAL);
		// the .50f is in order to build two columns of equal weight 
		LinearLayout.LayoutParams paramsLeftLayout
		= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT,.50f);
		
		left_layout.setLayoutParams(paramsLeftLayout);
		right_layout.setLayoutParams(paramsLeftLayout);
		
		int cnt=0;
		
		for(String par : ParametersName)
		{
		EditText edt = new EditText(context);
		edt.setTextColor(Color.BLACK);
		edt.setCursorVisible(true);
			
			if(cnt%2==0)				
				edt = addParameter(par, left_layout, context);
			else
				edt = addParameter(par, right_layout, context);
		
		parameter_field.put(par,edt);
		cnt++;
		}
		
		linearLayout.addView(left_layout);
		linearLayout.addView(right_layout);
		
		return linearLayout;
	}

    private EditText addParameter(String par, LinearLayout lay, Context context){
        
    	LinearLayout par_lay = new LinearLayout(context);
    	TextView par_text = new TextView(context);
    	EditText par_edit =new EditText(context);
    	
    	par_edit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	par_edit.setMaxEms(15);
    	par_edit.setMaxWidth(25);
    	
    	par_edit.setHint(parameter_default.get(par).toString());
    
    	TableLayout.LayoutParams params = new TableLayout.LayoutParams();
        params.setMargins(5, 5, 5, 5);
        par_edit.setLayoutParams(params );
        par_edit.setTextColor(Color.BLACK);
        
    	par_edit.setFocusable(true);
    	par_edit.setFocusableInTouchMode(true);
    //    InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	//	inputManager.showSoftInput(par_edit, 0);;
        
    	par_text.setText(parameter_short.get(par));
    	par_text.setTextColor(Color.DKGRAY);
    	par_text.setTextSize(16);
    	par_text.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    	
    	par_lay.addView(par_text);
    	par_lay.addView(par_edit);
    	lay.addView(par_lay);
    	
    	return par_edit;
    }
	
	public Map<String,Double> getValues(){
		Map<String,Double> values_map = new HashMap<String,Double>();
		
		for(String par : ParametersName)
			values_map.put(par, setParameter(par));
		
		return values_map; 
	}

    public double setParameter(String field){
    	//
    	if(parameter_field.get(field).getText().toString().equals(""))
			return parameter_default.get(field);
		else
			return Double.valueOf(parameter_field.get(field).getText().toString());

    }
    
    public boolean emptyString(String string){
    	if(string.compareTo("")==1)
    		return true;
    	else
    		return false;
    	
    }
	
	private Map<String,Double> parameter_default = new HashMap<String,Double>(20);
	public static Map<String,String> parameter_short = new HashMap<String,String>(20);
	private Map<String,EditText> parameter_field = new HashMap<String,EditText>(20);
	private double[] parameters=null;
	private String Title;
	private String[] ParametersName;
	private ArrayList<EditText> EditList=new ArrayList<EditText>();;
}
