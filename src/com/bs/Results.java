package com.bs;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Results {

	public Results(String title, String[] res_names){
				Title = title;
				ResultNames = res_names;
	}
	
	public LinearLayout buildResultsLayout(final Context context){
		
		LinearLayout.LayoutParams layoutResLinearLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(20, 20, 20, 20);
		linearLayout.setLayoutParams(layoutResLinearLayout);

		int cornerRadius = 5;
		float[] outerR = new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius};
		RectF inset = new RectF(4, 4, 4, 4);
		float[] innerR = new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius};
		ShapeDrawable circle = new ShapeDrawable(new RoundRectShape(outerR, inset, innerR));
		circle.setPadding(6, 0, 6, 0);

		//circle.getPaint().setColor(Color.rgb(46, 179, 235));
		circle.getPaint().setColor(Color.rgb(33, 90, 234));
		linearLayout.setBackgroundDrawable(circle); 	

		LinearLayout greeks_layout = new LinearLayout(context);
		greeks_layout.setOrientation(LinearLayout.HORIZONTAL);
		
		price_text = new TextView(context);
		price_text.setText(Title.concat(" PRICE:  "));
		price_text.setTextColor(Color.rgb(33, 90, 234));
		price_text.setTextSize(18);
		price_text.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		linearLayout.addView(price_text);
		
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
		int bck_cnt=0;
		
		for(String res : ResultNames)
		{
		
			TextView edt = new TextView(context);
			
			if(cnt%2==0){				
				edt=addGreek(left_layout,res.concat(":  "), context);
			}
			else{
				edt=addGreek(right_layout, res.concat(":  "), context);
			}
		if(bck_cnt<2){
			edt.setBackgroundColor(Color.LTGRAY);
			bck_cnt++;
		}else{
			edt.setBackgroundColor(Color.WHITE);
			bck_cnt++;
			if(bck_cnt==4){ bck_cnt=0;}
		}

		result_field.put(res, edt);
		cnt++;
		}
		
		greeks_layout.addView(left_layout);
		greeks_layout.addView(right_layout);		
		
		linearLayout.addView(greeks_layout);
		
		return linearLayout;
	}
	
	private TextView addGreek(LinearLayout lay, String res, Context context){
		TextView txt = new TextView(context);
		txt.setText(res);
    	txt.setTextColor(Color.DKGRAY);
		txt.setTextSize(16);
		txt.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    	   	
		txt.setPadding(1, 2, 1, 2);
		txt.setSingleLine();
		
    	lay.addView(txt);
    	
		return txt;
	}
	
	public void setResults(double[] res){
		Results = res;
	//ugly casting (Double)... do something better...
		price_text.setText(Title+" PRICE: "+((Double)res[0]).toString());
		result_field.get("Delta").setText("Delta:  "+((Double)res[1]).toString());
		result_field.get("Gamma").setText("Gamma:  "+((Double)res[2]).toString());
		result_field.get("Speed").setText("Speed:  "+((Double)res[3]).toString());
		result_field.get("Theta").setText("Theta:  "+((Double)res[4]).toString());
		result_field.get("Vega").setText("Vega:  "+((Double)res[5]).toString());
		result_field.get("Rho").setText("Rho:  "+((Double)res[6]).toString());
	
	}
	
	public void setDefaultText(String text){
		
		price_text.setText(text);
		
		result_field.get("Delta").setText("Delta:  ");
		result_field.get("Gamma").setText("Gamma:  ");
		result_field.get("Speed").setText("Speed:  ");
		result_field.get("Theta").setText("Theta:  ");
		result_field.get("Vega").setText("Vega:  ");
		result_field.get("Rho").setText("Rho:  ");
	}
	
	public void setTitle(String string) {
		// TODO Auto-generated method stub
		Title = string;
	}
	
	private String Title;
	private String[] ResultNames=null;
	private double[] Results=null;
	private TextView price_text;
	private Map<String,TextView> result_field = new HashMap<String,TextView>(20);

}
