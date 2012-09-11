package com.bs;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.text.Html;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;


public class BSMain extends Activity {

	final Context context=this;
	static int id = 1;
	private enum option_type {CALL, PUT, DIGITAL_CALL, DIGITAL_PUT, UP_IN_PUT, DOWN_IN_PUT,
		UP_IN_CALL, DOWN_IN_CALL, UP_OUT_PUT, DOWN_OUT_PUT,
		UP_OUT_CALL, DOWN_OUT_CALL, NULL_OPTION};
	private option_type option_flag;
	private Map<String,option_type> string_to_type = new HashMap<String,option_type>();
	private Parameters parameter;
	private Results result;
	private LinearLayout param_layout;
	private LinearLayout result_layout;
	
	
	 // Returns a valid id that isn't in use
   public int findId(){  
       View v = findViewById(id);  
       while (v != null){  
           v = findViewById(++id);  
       }  
       return id++;  
   }
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bsmain);      

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        init();
     
        //button listener
        addListenerOnButton();
        //info button listener
        addListenerOnInfoButton();
        //spinner listener
        addListenerOnSpinner();
        
        //issues with initial focus
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_bsmain, menu);
        return true;
    }
 

    private void init(){
    	//TODO: find a better solution...
    	//vanilla
    		string_to_type.put("CALL", option_type.CALL);	
    		string_to_type.put("PUT", option_type.PUT);	
    		//digital
    		string_to_type.put("DIGITAL CALL", option_type.DIGITAL_CALL);	
    		string_to_type.put("DIGITAL PUT", option_type.DIGITAL_PUT);	
    		//barrier
    		string_to_type.put("UP-IN CALL", option_type.UP_IN_CALL);	
    		string_to_type.put("DOWN-IN CALL", option_type.DOWN_IN_CALL);
    		string_to_type.put("UP-IN PUT", option_type.UP_IN_PUT);	
    		string_to_type.put("DOWN-IN PUT", option_type.DOWN_IN_PUT);
    		
    		string_to_type.put("UP-OUT CALL", option_type.UP_OUT_CALL);	
    		string_to_type.put("DOWN-OUT CALL", option_type.DOWN_OUT_CALL);
    		string_to_type.put("UP-OUT PUT", option_type.UP_OUT_PUT);	
    		string_to_type.put("DOWN-OUT PUT", option_type.DOWN_OUT_PUT);
    	
    		   String[] list = EuropeanCall.EuropeanParameters;       			        
    	        parameter = new Parameters("Vanilla call parameters", list);
    	        param_layout = parameter.buildParametersLayout(this.getBaseContext()); 
    	        param_layout.setPadding(10, 10, 10, 10);
    	        
    	       LinearLayout  layout=(LinearLayout) this.findViewById(R.id.parameter_layout);
    	       layout.addView(param_layout);	
    	       
    	       String[] greek_list = getResources().getStringArray(R.array.greek_array);; 
    	       
    	       result = new Results("CALL", greek_list);
    	       result_layout = result.buildResultsLayout(this.getBaseContext());
    	       result_layout.setPadding(10, 10, 10, 10);
    	       
    	       result_layout.setFocusable(true);
    	       result_layout.setFocusableInTouchMode(true);
    	       
    	       LinearLayout  res_line=(LinearLayout) this.findViewById(R.id.result_line);
    	   //    res_line.setBackgroundColor(Color.WHITE);
    	       res_line.addView(result_layout);

    	    
    	}
    
    private void changeParameter(String name){
		String[] list;
		LinearLayout  layout;
		
		layout=(LinearLayout) this.findViewById(R.id.parameter_layout);
		layout.removeView(param_layout);
		
		switch(string_to_type.get(name)){
		
		case CALL:
			option_flag = option_type.CALL;
	        list  = EuropeanCall.EuropeanParameters;       			        
	        parameter = new Parameters("Vanilla call parameters", list);

			break;
		case PUT:
			option_flag = option_type.PUT;
	        list  = EuropeanPut.EuropeanParameters;       			        
 	        parameter = new Parameters("Vanilla put parameters", list);
			break;
			
		case DIGITAL_CALL:
			option_flag = option_type.DIGITAL_CALL;
	        list  = EuropeanDigitalCall.EuropeanParameters;       			        
 	        parameter = new Parameters("parameters", list);

			break;

		case DIGITAL_PUT:
			option_flag = option_type.DIGITAL_PUT;
	        list  = EuropeanDigitalPut.EuropeanParameters;       			        
 	        parameter = new Parameters("parameters", list);

			break;
		
		case UP_IN_CALL:
			option_flag = option_type.UP_IN_CALL;
			 list = EuropeanBarrierOption.EuropeanParameters;  
 	        parameter = new Parameters("barrier parameters", list, EuropeanUpInCall.UpInDefault); 
			break;
			
		case DOWN_IN_CALL:
			option_flag = option_type.DOWN_IN_CALL;
			 list = EuropeanBarrierOption.EuropeanParameters;  
 	        parameter = new Parameters("barrier parameters", list, EuropeanDownInCall.DownInDefault);
			break;
			
		case UP_IN_PUT:
			option_flag = option_type.UP_IN_PUT;
			 list = EuropeanBarrierOption.EuropeanParameters;  
 	        parameter = new Parameters("barrier parameters", list, EuropeanUpInCall.UpInDefault); 
			break;
			
		case DOWN_IN_PUT:
			option_flag = option_type.DOWN_IN_PUT;
			 list = EuropeanBarrierOption.EuropeanParameters;  
 	        parameter = new Parameters("barrier parameters", list, EuropeanDownInCall.DownInDefault);
			break;
			
		case UP_OUT_CALL:
			option_flag = option_type.UP_OUT_CALL;
			 list = EuropeanBarrierOption.EuropeanParameters;  
 	        parameter = new Parameters("barrier parameters", list, EuropeanUpOutCall.UpOutDefault); 
			break;
			
		case DOWN_OUT_CALL:
			option_flag = option_type.DOWN_OUT_CALL;
			 list = EuropeanBarrierOption.EuropeanParameters;  
 	        parameter = new Parameters("barrier parameters", list, EuropeanDownOutCall.DownOutDefault);
			break;
			
		case UP_OUT_PUT:
			option_flag = option_type.UP_OUT_PUT;
			 list = EuropeanBarrierOption.EuropeanParameters;  
 	        parameter = new Parameters("barrier parameters", list, EuropeanUpOutCall.UpOutDefault); 
			break;
			
		case DOWN_OUT_PUT:
			option_flag = option_type.DOWN_OUT_PUT;
			 list = EuropeanBarrierOption.EuropeanParameters;  
 	        parameter = new Parameters("barrier parameters", list, EuropeanDownOutCall.DownOutDefault);
			break;	
			
 	    default:
 	    	//do call at least...
 	    	option_flag = option_type.NULL_OPTION;
	        list  = EuropeanCall.EuropeanParameters;       			        
	        parameter = new Parameters("Vanilla call parameters", list);
 	    	break;
		}
	        param_layout = parameter.buildParametersLayout(this.getBaseContext()); 
	       //move to Parameters.java
	        param_layout.setPadding(10, 10, 10, 10);	       
	        layout.addView(param_layout);
    }
    
    private void addListenerOnSpinner(){
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
  
        spinner.setOnItemSelectedListener(
        		new AdapterView.OnItemSelectedListener(){
        			public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        			
        				result.setDefaultText(parent.getSelectedItem().toString().concat(" PRICE: "));	
        				changeParameter(parent.getSelectedItem().toString());
        				result.setTitle(parent.getSelectedItem().toString());
        		
        				
        			}
        		       public void onNothingSelected(AdapterView<?> parent) {
        		            // do default: call option
        		        }
        			
        		}
        		);
    	
    }
    
    private void addListenerOnInfoButton(){
    	Button button = (Button) findViewById(R.id.info_button);

    	button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				buildInfoDialog(option_flag);
				
			}
    		
    		
    	});
    }
    
    private void buildExceptionBarrierOptionDialog(String message, String action){
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
    	alertDialogBuilder.setTitle("Input Exception");
    	String full_message = message + "\n" + action;
    	alertDialogBuilder.setMessage(full_message);
    	
		alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {

			}
		  });
		
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
    }
    
    private void buildInfoDialog(option_type flag){
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
    	switch(flag){
    	
    	case CALL:
    		alertDialogBuilder.setTitle("Vanilla Call Option");
    		alertDialogBuilder.setMessage(getText(R.string.vanilla_description));
    		break;
    	case PUT:
    		alertDialogBuilder.setTitle("Vanilla Put Option");
    		alertDialogBuilder.setMessage(getText(R.string.vanilla_description));
    		break;	

    	case DIGITAL_CALL:
    		alertDialogBuilder.setTitle("Digital Call Option");
    		alertDialogBuilder.setMessage(getText(R.string.digital_description));
    		break;
    	case DIGITAL_PUT:
    		alertDialogBuilder.setTitle("Digital Put Option");
    		alertDialogBuilder.setMessage(getText(R.string.digital_description));
    		break;	
//barrier
    	case UP_IN_CALL:
    		alertDialogBuilder.setTitle("Barrier Up and In Call");
    		alertDialogBuilder.setMessage(getText(R.string.barrier_description));
    		break;
    	case DOWN_IN_CALL:
    		alertDialogBuilder.setTitle("Barrier Down and In Call");
    		alertDialogBuilder.setMessage(getText(R.string.barrier_description));
    		break;
    	case UP_IN_PUT:
    		alertDialogBuilder.setTitle("Barrier Up and In Put");
    		alertDialogBuilder.setMessage(getText(R.string.barrier_description));
    		break;
    	case DOWN_IN_PUT:
    		alertDialogBuilder.setTitle("Barrier Down and In Put");
    		alertDialogBuilder.setMessage(getText(R.string.barrier_description));
    		break;	
    		
    	case UP_OUT_CALL:
    		alertDialogBuilder.setTitle("Barrier Up and Out Call");
    		alertDialogBuilder.setMessage(getText(R.string.barrier_description));
    		break;
    	case DOWN_OUT_CALL:
    		alertDialogBuilder.setTitle("Barrier Down and Out Call");
    		alertDialogBuilder.setMessage(getText(R.string.barrier_description));
    		break;
    	case UP_OUT_PUT:
    		alertDialogBuilder.setTitle("Barrier Up and Out Put");
    		alertDialogBuilder.setMessage(getText(R.string.barrier_description));
    		break;
    	case DOWN_OUT_PUT:
    		alertDialogBuilder.setTitle("Barrier Down and Out Put");
    		alertDialogBuilder.setMessage(getText(R.string.barrier_description));
    		break;	

    	default:
    		alertDialogBuilder.setTitle("Undefined option type");
    		break;
    	}
    	
		alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, close
				// current activity
		//		BSMain.this.finish();
			}
		  });
		
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
    }
    
	private void addListenerOnButton() {
		 
		Button button = (Button) findViewById(R.id.compute_price);
 
		button.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
	
				InputMethodManager inputManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

				//get parameters
				Map<String,Double> values_map = new HashMap<String,Double>();
				values_map = parameter.getValues();
				
				//values_map.containsKey(key)
				//WARNING: the option can be non-initialized 
				EuropeanOption option=null;
				
				switch(option_flag){
					case CALL:
						option = new EuropeanCall(values_map.get("SPOT"),values_map.get("STRIKE"),
								values_map.get("RATE"),values_map.get("DIVIDEND"),
								values_map.get("EXPIRY"), values_map.get("VOLATILITY"));
						break;
						
					case PUT:
						option = new EuropeanPut(values_map.get("SPOT"),values_map.get("STRIKE"),
								values_map.get("RATE"),values_map.get("DIVIDEND"),
								values_map.get("EXPIRY"), values_map.get("VOLATILITY"));
						break;
											
					case DIGITAL_CALL:
						option = new EuropeanDigitalCall(values_map.get("SPOT"),values_map.get("STRIKE"),
								values_map.get("RATE"),values_map.get("DIVIDEND"),
								values_map.get("EXPIRY"), values_map.get("VOLATILITY"));
						break;	
						
					case DIGITAL_PUT:
						option = new EuropeanDigitalPut(values_map.get("SPOT"),values_map.get("STRIKE"),
								values_map.get("RATE"),values_map.get("DIVIDEND"),
								values_map.get("EXPIRY"), values_map.get("VOLATILITY"));
						break;	
					
					case UP_IN_CALL:
						try{
						option = new EuropeanUpInCall(values_map.get("SPOT"),values_map.get("STRIKE"),
								values_map.get("RATE"),values_map.get("DIVIDEND"),
								values_map.get("EXPIRY"), values_map.get("VOLATILITY"),
								values_map.get("BARRIER"), values_map.get("REBATE"));
						}catch(IllegalArgumentException e){
							
						buildExceptionBarrierOptionDialog(e.getMessage(),
								"Converting the option to Vanilla Call...");
						//convert to vanilla call
						option = new EuropeanCall(values_map.get("SPOT"),values_map.get("STRIKE"),
								values_map.get("RATE"),values_map.get("DIVIDEND"),
								values_map.get("EXPIRY"), values_map.get("VOLATILITY"));
						}
						break;		
						
					case DOWN_IN_CALL:
						try{
						option = new EuropeanDownInCall(values_map.get("SPOT"),values_map.get("STRIKE"),
								values_map.get("RATE"),values_map.get("DIVIDEND"),
								values_map.get("EXPIRY"), values_map.get("VOLATILITY"),
								values_map.get("BARRIER"), values_map.get("REBATE"));
						}catch(IllegalArgumentException e){
							buildExceptionBarrierOptionDialog(e.getMessage(),
									"Converting the option to Vanilla Call...");
							//convert to vanilla call
							option = new EuropeanCall(values_map.get("SPOT"),values_map.get("STRIKE"),
									values_map.get("RATE"),values_map.get("DIVIDEND"),
									values_map.get("EXPIRY"), values_map.get("VOLATILITY"));
						}
						break;
						
					case UP_IN_PUT:
						try{
						option = new EuropeanUpInPut(values_map.get("SPOT"),values_map.get("STRIKE"),
								values_map.get("RATE"),values_map.get("DIVIDEND"),
								values_map.get("EXPIRY"), values_map.get("VOLATILITY"),
								values_map.get("BARRIER"), values_map.get("REBATE"));
						}catch(IllegalArgumentException e){
							
						buildExceptionBarrierOptionDialog(e.getMessage(),
								"Converting the option to Vanilla Put...");
						//convert to vanilla put
						option = new EuropeanPut(values_map.get("SPOT"),values_map.get("STRIKE"),
								values_map.get("RATE"),values_map.get("DIVIDEND"),
								values_map.get("EXPIRY"), values_map.get("VOLATILITY"));
						}
						break;		
						
					case DOWN_IN_PUT:
						try{
						option = new EuropeanDownInPut(values_map.get("SPOT"),values_map.get("STRIKE"),
								values_map.get("RATE"),values_map.get("DIVIDEND"),
								values_map.get("EXPIRY"), values_map.get("VOLATILITY"),
								values_map.get("BARRIER"), values_map.get("REBATE"));
						}catch(IllegalArgumentException e){
							buildExceptionBarrierOptionDialog(e.getMessage(),
									"Converting the option to Vanilla Put...");
							//convert to vanilla put
							option = new EuropeanPut(values_map.get("SPOT"),values_map.get("STRIKE"),
									values_map.get("RATE"),values_map.get("DIVIDEND"),
									values_map.get("EXPIRY"), values_map.get("VOLATILITY"));
						}
						break;
						
					case UP_OUT_CALL:
						try{
						option = new EuropeanUpOutCall(values_map.get("SPOT"),values_map.get("STRIKE"),
								values_map.get("RATE"),values_map.get("DIVIDEND"),
								values_map.get("EXPIRY"), values_map.get("VOLATILITY"),
								values_map.get("BARRIER"), values_map.get("REBATE"));
						}catch(IllegalArgumentException e){
							
						buildExceptionBarrierOptionDialog(e.getMessage(),
								"Converting the option to Vanilla Call...");
						//convert to vanilla call
						option = new EuropeanCall(values_map.get("SPOT"),values_map.get("STRIKE"),
								values_map.get("RATE"),values_map.get("DIVIDEND"),
								values_map.get("EXPIRY"), values_map.get("VOLATILITY"));
						}
						break;		
						
					case DOWN_OUT_CALL:
						try{
						option = new EuropeanDownOutCall(values_map.get("SPOT"),values_map.get("STRIKE"),
								values_map.get("RATE"),values_map.get("DIVIDEND"),
								values_map.get("EXPIRY"), values_map.get("VOLATILITY"),
								values_map.get("BARRIER"), values_map.get("REBATE"));
						}catch(IllegalArgumentException e){
							buildExceptionBarrierOptionDialog(e.getMessage(),
									"Converting the option to Vanilla Call...");
							//convert to vanilla call
							option = new EuropeanCall(values_map.get("SPOT"),values_map.get("STRIKE"),
									values_map.get("RATE"),values_map.get("DIVIDEND"),
									values_map.get("EXPIRY"), values_map.get("VOLATILITY"));
						}
						break;
						
					case UP_OUT_PUT:
						try{
						option = new EuropeanUpOutPut(values_map.get("SPOT"),values_map.get("STRIKE"),
								values_map.get("RATE"),values_map.get("DIVIDEND"),
								values_map.get("EXPIRY"), values_map.get("VOLATILITY"),
								values_map.get("BARRIER"), values_map.get("REBATE"));
						}catch(IllegalArgumentException e){
							
						buildExceptionBarrierOptionDialog(e.getMessage(),
								"Converting the option to Vanilla Put...");
						//convert to vanilla put
						option = new EuropeanPut(values_map.get("SPOT"),values_map.get("STRIKE"),
								values_map.get("RATE"),values_map.get("DIVIDEND"),
								values_map.get("EXPIRY"), values_map.get("VOLATILITY"));
						}
						break;		
						
					case DOWN_OUT_PUT:
						try{
						option = new EuropeanDownOutPut(values_map.get("SPOT"),values_map.get("STRIKE"),
								values_map.get("RATE"),values_map.get("DIVIDEND"),
								values_map.get("EXPIRY"), values_map.get("VOLATILITY"),
								values_map.get("BARRIER"), values_map.get("REBATE"));
						}catch(IllegalArgumentException e){
							buildExceptionBarrierOptionDialog(e.getMessage(),
									"Converting the option to Vanilla Put...");
							//convert to vanilla put
							option = new EuropeanPut(values_map.get("SPOT"),values_map.get("STRIKE"),
									values_map.get("RATE"),values_map.get("DIVIDEND"),
									values_map.get("EXPIRY"), values_map.get("VOLATILITY"));
						}
						break;

					default:
						option_flag = option_type.NULL_OPTION;
						break;
				}

				//build the option
			
			   
				if(option_flag!=option_type.NULL_OPTION){
				  //compute price
				     double price = option.getPrice();
				     double delta = option.getDelta();
				     double gamma = option.getGamma();
				     double speed = option.getSpeed();
				     double theta = option.getTheta();
				     double vega = option.getVega();
				     double rho = option.getRho();
				     
				     //show result 
				     double[] res = {truncate(price,6),truncate(delta,6),truncate(gamma,6),
				    		 truncate(speed,6),truncate(theta,6),truncate(vega,6),truncate(rho,6)};
				     result.setResults(res);
				   //finally change focus				
					result_layout.requestFocus();	
				}else{Toast.makeText(getApplicationContext(), "Unrecognized option type...", Toast.LENGTH_LONG).show();}

					
			}
 
		});
 
	}
    
	public static double truncate(double value, int places) {
//	BigDecimal trunc = new BigDecimal(value);
		//trunc.setScale(1,BigDecimal.ROUND_HALF_EVEN);
	 double multiplier = Math.pow(10, places);
	    return Math.floor(multiplier * value) / multiplier;
		//return trunc;
	}   
}
