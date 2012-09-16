package com.bs;

import static java.lang.Math.log;
import static java.lang.Math.sqrt;
import static java.lang.Math.exp;
import static java.lang.Math.pow;

//pricing formulas and notation from Espen Gaarder Huang - The Complete Guide to Option Pricing Formula - 2006
public class EuropeanBarrierOption  {

	public	EuropeanBarrierOption(double Spot_, double Strike_, double r_, 
			double d_, double T_, double vol_, double Barrier_, double Rebate_){
		Spot = Spot_;
		Strike = Strike_;
		r = r_;
		// d IS NOT d_ !!
		d = r_-d_;
		
		//d-r does NOT depend on r!!
		Expiry = T_;
		Vol = vol_;
		Barrier = Barrier_;
		Rebate = Rebate_;
		//pre-compute these
		standardDeviation = Vol*sqrt(Expiry);

		mu = d/(Vol*Vol)-0.5;
		//TODO check this
		//is that correct? 
		//look at http://www.global-derivatives.com/index.php/component/content/13?task=view
		lambda = sqrt(mu*mu + 2.*r/(Vol*Vol));
		
		x1 = log(Spot/Strike)/standardDeviation +(1+mu)*standardDeviation;
		x2 = log(Spot/Barrier)/standardDeviation + (1+mu)*standardDeviation;
		y1 = log(Barrier*Barrier/(Spot*Strike))/standardDeviation+(1+mu)*standardDeviation;
		y2 = log(Barrier/Spot)/standardDeviation+(1+mu)*standardDeviation;
		z = log(Barrier/Spot)/standardDeviation +lambda*standardDeviation;

	}
	
	private double DerX1(EuropeanBarrierParams par){
		double der = 0.0;
		switch(par){
			case EXPIRY:
				//I'm deriving with respect to time, NOT EXPIRY (tau = T-t, change sign)
				der = -0.5*log(Spot/Strike)/standardDeviation/Expiry + 0.5*(1+mu)*standardDeviation/Expiry;
				der = -der;
				break;
				
			case VOLATILITY:
				double d_mu = -2*d/(Vol*Vol*Vol);
				der = -log(Spot/Strike)/standardDeviation/Vol + (1+mu)*sqrt(Expiry)+(1+d_mu)*standardDeviation;
				break;
			default:
				break;
		
		}
		
		return der;
	}
	
	private double DerX2(EuropeanBarrierParams par){
		double der = 0.0;
		switch(par){
			case EXPIRY:
				//I'm deriving with respect to time, NOT EXPIRY (tau = T-t, change sign)
				der = -0.5*log(Spot/Barrier)/standardDeviation/Expiry + 0.5*(1+mu)*standardDeviation/Expiry;
				der = -der;
				break;
				
			case VOLATILITY:
				double d_mu = -2*d/(Vol*Vol*Vol);
				der = -log(Spot/Barrier)/standardDeviation/Vol + (1+mu)*sqrt(Expiry)+(1+d_mu)*standardDeviation;
				break;
				
			default:
				break;
		
		}		
		return der;
	}	

	private double DerY1(EuropeanBarrierParams par){
		double der = 0.0;
		switch(par){
			case EXPIRY:
				//I'm deriving with respect to time, NOT EXPIRY (tau = T-t, change sign)
				der = -0.5*log(Barrier*Barrier/(Spot*Expiry))/standardDeviation/Expiry + 0.5*(1+mu)*standardDeviation/Expiry;
				der = -der;
				break;
				
			case VOLATILITY:
				double d_mu = -2*d/(Vol*Vol*Vol);
				der = -log(Barrier*Barrier/(Spot*Expiry))/standardDeviation/Vol + (1+mu)*sqrt(Expiry)+(1+d_mu)*standardDeviation;
				break;
				
			default:
				break;
		
		}		
		return der;
	}	
	
	private double DerY2(EuropeanBarrierParams par){
		double der = 0.0;
		switch(par){
			case EXPIRY:
				//I'm deriving with respect to time, NOT EXPIRY (tau = T-t, change sign)
				der = -0.5*log(Barrier/Spot)/standardDeviation/Expiry + 0.5*(1+mu)*standardDeviation/Expiry;
				der = -der;
				break;
				
			case VOLATILITY:
				double d_mu = -2*d/(Vol*Vol*Vol);
				der = -log(Barrier/Spot)/standardDeviation/Vol + (1+mu)*sqrt(Expiry)+(1+d_mu)*standardDeviation;
				break;
				
			default:
				break;
		
		}		
		return der;
	}		

	private double DerZ(EuropeanBarrierParams par){
		double der = 0.0;
		switch(par){
			case EXPIRY:
				//I'm deriving with respect to time, NOT EXPIRY (tau = T-t, change sign)
				der = -0.5*log(Barrier/Spot)/standardDeviation/Expiry + 0.5*lambda*standardDeviation/Expiry;
				der = -der;
				break;
				
			case VOLATILITY:
				double d_mu = -2*d/(Vol*Vol*Vol);
				double d_lambda = 0.5*(2*mu*d_mu - 4*r/(Vol*Vol*Vol))/lambda;
				der = -log(Barrier/Spot)/standardDeviation/Vol + lambda*sqrt(Expiry)+d_lambda*standardDeviation;
				break;
				
			default:
				break;
		
		}		
		return der;
	}	
	//gaarder ABCDEF factors
	protected double A(int psi){
		return + psi*Spot*exp((d-r)*Expiry)*Normals.CumulativeNormal(psi*x1)
				- psi*Strike*exp(-r*Expiry)*Normals.CumulativeNormal(psi*x1-psi*standardDeviation);
	}
	
	protected double B(int psi){
		return + psi*Spot*exp((d-r)*Expiry)*Normals.CumulativeNormal(psi*x2)
				- psi*Strike*exp(-r*Expiry)*Normals.CumulativeNormal(psi*x2-psi*standardDeviation);
	}
	
	protected double C(int psi, int eta){		
		return + psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))*Normals.CumulativeNormal(eta*y1)
				- psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)*Normals.CumulativeNormal(eta*y1-eta*standardDeviation);
	}
	
	protected double D(int psi, int eta){
		return + psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))*Normals.CumulativeNormal(eta*y2)
				- psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
				*Normals.CumulativeNormal(eta*y2-eta*standardDeviation);		
	}
	
	protected double E(int eta){
		double par = Normals.CumulativeNormal(eta*x2 - eta*standardDeviation) 
					- pow(Barrier/Spot,2.*mu)*Normals.CumulativeNormal(eta*y2-eta*standardDeviation);
		return Rebate*exp(-r*Expiry)*par;
	}

	protected double F(int eta){
		double par = pow(Barrier/Spot,mu+lambda)*Normals.CumulativeNormal(eta*z)
				+pow(Barrier/Spot,mu-lambda)*Normals.CumulativeNormal(eta*z-2.*eta*lambda*standardDeviation);
		return Rebate*par;
	}

	//derivatives
	protected double DerA(int ord, EuropeanBarrierParams par, int psi){
		double der = 0.0;
		switch(par){
		    case SPOT:
		    	switch(ord){
		    	case 1:
		    		// checked
		    		der = psi*exp((d-r)*Expiry)*Normals.CumulativeNormal(psi*x1);
		    		break;
		    	case 2:
		    		der= exp((d-r)*Expiry)*Normals.NormalDensity(x1)/(Spot*standardDeviation);
		    		break;
		    	case 3:
		    		//ok
		    		der= exp((d-r)*Expiry)*Normals.NormalDensity(x1)/(Spot*standardDeviation)*(-x1/(Spot*standardDeviation))
		    			+exp((d-r)*Expiry)*Normals.NormalDensity(x1)/(Spot*standardDeviation)*(-1/Spot);
		    		break;
		    	default:
		    		
		    		break;
		    	}
		    	break;
		    	
		    case EXPIRY:
		    	//sensitivity to time tau = T-t: derive respect T and change sign		    	
		    	der = -(d-r)*psi*Spot*exp((d-r)*Expiry)*Normals.CumulativeNormal(psi*x1)
		    			+ psi*Spot*exp((d-r)*Expiry)*Normals.NormalDensity(x1)*psi*DerX1(par)
		    	
						- r*psi*Strike*exp(-r*Expiry)*Normals.CumulativeNormal(psi*x1-psi*standardDeviation)
						- psi*Strike*exp(-r*Expiry)
						*Normals.NormalDensity(x1-standardDeviation)*psi*(DerX1(par)+0.5*standardDeviation/Expiry);
		    	break;
		    
		    case VOLATILITY:				
				der = + psi*Spot*exp((d-r)*Expiry)*Normals.NormalDensity(x1)*psi*DerX1(par)
						- psi*Strike*exp(-r*Expiry)
						*Normals.NormalDensity(x1-standardDeviation)*psi*(DerX1(par)-sqrt(Expiry));
		    	
		    	
		    	break;
		    	
		    case RATE:	    	
		    	der = +psi*Spot*exp((d-r)*Expiry)*Normals.NormalDensity(x1)*psi*standardDeviation/(Vol*Vol)
						+ Expiry*psi*Strike*exp(-r*Expiry)*Normals.CumulativeNormal(psi*x1-psi*standardDeviation)
						
						-psi*Strike*exp(-r*Expiry)*Normals.NormalDensity(x1-standardDeviation)*psi*standardDeviation/(Vol*Vol);
		  
		    	break;
		
			default:
				//TODO
			//throw error!!
			break;
		}
		
		return der;
	}

	protected double DerB(int ord, EuropeanBarrierParams par, int psi){
		double der = 0.0;
 		double pre = psi*exp((d-r)*Expiry);
 		double N1 = Normals.CumulativeNormal(psi*x2);
 		double n1 = Normals.NormalDensity(psi*x2);
 		double N2 = Normals.CumulativeNormal(psi*x2-psi*standardDeviation);
 		double n2 = Normals.NormalDensity(x2-standardDeviation);
 		double d_x2,dd_x2,ddd_x2,d_N1,dd_N1,ddd_N1,d_N2,dd_N2,ddd_N2;
 		
		switch(par){
		    case SPOT:
			    d_x2 = 1/(Spot*standardDeviation);
		    	dd_x2 = -1/(Spot*Spot*standardDeviation);
		    	ddd_x2 = +2/(Spot*Spot*Spot*standardDeviation);
		    	
		    	d_N1 = n1*psi*d_x2;
		    	dd_N1 = n1*psi*(dd_x2 - (x2)*d_x2*d_x2);
		    	ddd_N1 = n1*psi*(ddd_x2 - 3*dd_x2*d_x2*(x2)
		    					- d_x2*d_x2*d_x2 + d_x2*d_x2*d_x2*(x2)*(x2) );
		    	
		    	d_N2 = n2*psi*d_x2;
		    	dd_N2 = n2*psi*(dd_x2 - (x2-standardDeviation)*d_x2*d_x2);
		    	ddd_N2 = n2*psi*(ddd_x2 - 3*dd_x2*d_x2*(x2-standardDeviation)
		    					- d_x2*d_x2*d_x2 + d_x2*d_x2*d_x2*(x2-standardDeviation)*(x2-standardDeviation) );
		    	switch(ord){
		    	case 1:	   
		    		der = pre*Normals.CumulativeNormal(psi*x2)
		    				+ pre*psi*(1-Strike/Barrier)*Normals.NormalDensity(x2)/standardDeviation;
		    		  
		    		break;
		    	case 2:
		    		der = pre*Normals.NormalDensity(x2)/(Spot*standardDeviation)
		    			+ pre*psi*(1-Strike/Barrier)*Normals.NormalDensity(x2)/standardDeviation
		    			*(-x2)/(Spot*standardDeviation);
		    		break;
		    	case 3:
		    		der = pre*(Spot*ddd_N1  + 3*dd_N1)
		    			-psi*exp(-r*Expiry)*Strike*ddd_N2;
		    		break;
		    	default:
		    		//TODO: 
		    		break;
		    	}
		    	break;
		    	
		    case EXPIRY:
		    	//sensitivity to time tau = T-t: derive respect T and change sign		    	
		    	der = -(d-r)*psi*Spot*exp((d-r)*Expiry)*Normals.CumulativeNormal(psi*x2)
		    			+ psi*Spot*exp((d-r)*Expiry)*Normals.NormalDensity(x2)*psi*DerX2(par)
		    			
		    			- r*psi*Strike*exp(-r*Expiry)*Normals.CumulativeNormal(psi*x2-psi*standardDeviation)
						 - psi*Strike*exp(-r*Expiry)
						 *Normals.NormalDensity(x2-standardDeviation)*psi*(DerX2(par)+0.5*standardDeviation/Expiry);
		    	break;
		    
		    case VOLATILITY:		    					
		    	der = + psi*Spot*exp((d-r)*Expiry)*Normals.NormalDensity(x2)*psi*DerX2(par)
		    		- psi*Strike*exp(-r*Expiry)*Normals.NormalDensity(x2-standardDeviation)*psi*(DerX2(par)-sqrt(Expiry));
		    	break;
		    	
		    case RATE:
		    	der =  +psi*Spot*exp((d-r)*Expiry)*Normals.NormalDensity(x2)*psi*standardDeviation/(Vol*Vol)
						+ Expiry*psi*Strike*exp(-r*Expiry)*Normals.CumulativeNormal(psi*x2-psi*standardDeviation)
						
						-psi*Strike*exp(-r*Expiry)*Normals.NormalDensity(x2-standardDeviation)*psi*standardDeviation/(Vol*Vol);
		    	break;
		
			default:
			//throw error!!
			break;
		}
		
		return der;
	}	
	
	protected double DerC(int ord, EuropeanBarrierParams par, int psi, int eta){
		double der = 0.0;
		double d_mu=0.0;
		double d_pw1,dd_pw1,ddd_pw1,d_pw2,dd_pw2,ddd_pw2,
		d_N1,dd_N1,ddd_N1,d_N2,dd_N2,ddd_N2,d_y1,dd_y1,ddd_y1;
	    double pw1 = 	Spot*pow(Barrier/Spot,2.*(mu+1));
	    double pw2 =  pow(Barrier/Spot,2.*mu);
	    double N1 = Normals.CumulativeNormal(eta*y1);
	    double N2 = Normals.CumulativeNormal(eta*y1-eta*standardDeviation);
	    double n1 = Normals.NormalDensity(y1);
	    double n2 = Normals.NormalDensity(y1-standardDeviation);
		switch(par){
		    case SPOT:    
		    				    
			    d_pw1 = -(2*mu+1)/Spot*pw1;
			    dd_pw1 = 2.*(mu+1)*(2*mu+1)/(Spot*Spot)*pw1;
			    ddd_pw1 = -(2*mu+3)*2.*(mu+1)*(2*mu+1)/(Spot*Spot*Spot)*pw1;
			    
			    d_pw2 = -2*mu/Spot*pw2;
			    dd_pw2 = (2*mu+1)*2*mu/(Spot*Spot)*pw2;
			    ddd_pw2 = -2*(mu+1)*(2*mu+1)*2*mu/(Spot*Spot*Spot)*pw2;
			    
			    d_y1 = -1/(Spot*standardDeviation);
		    	dd_y1 = +1/(Spot*Spot*standardDeviation);
		    	ddd_y1 = -2/(Spot*Spot*Spot*standardDeviation);
		    	
		    	d_N1 = n1*eta*d_y1;
		    	dd_N1 = n1*eta*(dd_y1 - (y1)*d_y1*d_y1);
		    	ddd_N1 = n1*eta*(ddd_y1 - 3*dd_y1*d_y1*(y1)
		    					- d_y1*d_y1*d_y1 + d_y1*d_y1*d_y1*(y1)*(y1) );
		    	
		    	d_N2 = n2*eta*d_y1;
		    	dd_N2 = n2*eta*(dd_y1 - (y1-standardDeviation)*d_y1*d_y1);
		    	ddd_N2 = n2*eta*(ddd_y1 - 3*dd_y1*d_y1*(y1-standardDeviation)
		    					- d_y1*d_y1*d_y1 + d_y1*d_y1*d_y1*(y1-standardDeviation)*(y1-standardDeviation));

		    	switch(ord){

		    	
		    	case 1:
		    	// checked
		    		der = -(2.*mu+1.)*psi*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))
		    		*Normals.CumulativeNormal(eta*y1)
		    		+ 2.*mu*psi*Strike/Spot*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
		    		*Normals.CumulativeNormal(eta*y1-eta*standardDeviation);
		    		  
		    		break;
		    	case 2:
		    		der = -(2.*mu+1.)*psi*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))
		    			*(-2.*(mu+1.)/Spot)*Normals.CumulativeNormal(eta*y1)
		    			
		    			-(2.*mu+1.)*psi*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))
		    			*eta*Normals.NormalDensity(y1)/(-Spot*standardDeviation)
		    			
		    			-2*mu*(2*mu+1)*psi*Strike/(Spot*Spot)*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
		    			*Normals.CumulativeNormal(eta*y1-eta*standardDeviation)
		    			
		    			+2*mu*psi*Strike/Spot*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
		    			*Normals.NormalDensity(y1-standardDeviation)*eta/(-Spot*standardDeviation);
		    		break;
		    	case 3:
		    		//checked
		    		der = psi*exp((d-r)*Expiry)*(pw1*ddd_N1 + N1*ddd_pw1 + 3*d_pw1*dd_N1 + 3*dd_pw1*d_N1)
		    			- psi*Strike*exp(-r*Expiry)*(pw2*ddd_N2 + N2*ddd_pw2 + 3*d_pw2*dd_N2 + 3*dd_pw2*d_N2);
		    		break;
		    	default:
		  
		    		break;
		    	}
		    	break;
		
		    case EXPIRY:
		    	//sensitivity to time tau = T-t: derive respect T and change sign	
		    	der =  -(d-r)*psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))*Normals.CumulativeNormal(eta*y1)
		    			+ psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))
		    			*Normals.NormalDensity(y1)*eta*DerY1(par)
		    			
		    			- r*psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
				*Normals.CumulativeNormal(eta*y1-eta*standardDeviation)
					- psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
				*Normals.NormalDensity(y1-standardDeviation)*eta*(DerY1(par)+0.5*standardDeviation/Expiry);
		    	
		    	break;
		    
		    case VOLATILITY:
		    	 d_mu = -2*d/(Vol*Vol*Vol);
		    	der = + log(Barrier/Spot)*2*d_mu*psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))
		    			*Normals.CumulativeNormal(eta*y1)
		    		+ psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))
		    		*Normals.NormalDensity(y1)*eta*DerY1(par)
		    		- log(Barrier/Spot)*2*d_mu*psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
				*Normals.CumulativeNormal(eta*y1-eta*standardDeviation)
		    					- psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
				*Normals.NormalDensity(y1-standardDeviation)*eta*(DerY1(par)-sqrt(Expiry));
		    			
		    	break;
		    	
		    case RATE:
		    	der =psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))*log(Barrier/Spot)*(2/(Vol*Vol))
		    			*Normals.CumulativeNormal(eta*y1)
		    		+psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))
	    			*Normals.NormalDensity(y1)*eta*standardDeviation/(Vol*Vol)	
 			
				+ Expiry*psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
				*Normals.CumulativeNormal(eta*y1-eta*standardDeviation)
		    	- psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)*log(Barrier/Spot)*(2/(Vol*Vol))
		    	*Normals.CumulativeNormal(eta*y1-eta*standardDeviation)
		    	- psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
		    	*Normals.NormalDensity(y1-standardDeviation)*eta*standardDeviation/(Vol*Vol);
		    	break;
		
		    	
			default:
				//TODO
			//throw error!!
			break;
		}
		
		return der;
	}		

	protected double DerD(int ord, EuropeanBarrierParams par, int psi, int eta){
		double der = 0.0;
		double d_pw1,dd_pw1,ddd_pw1,d_pw2,dd_pw2,ddd_pw2,
		d_N1,dd_N1,ddd_N1,d_N2,dd_N2,ddd_N2,d_y2,dd_y2,ddd_y2;
	    double pw1 = 	Spot*pow(Barrier/Spot,2.*(mu+1));
	    double pw2 =  pow(Barrier/Spot,2.*mu);
	    double N1 = Normals.CumulativeNormal(eta*y2);
	    double N2 = Normals.CumulativeNormal(eta*y2-eta*standardDeviation);
	    double n1 = Normals.NormalDensity(y2);
	    double n2 = Normals.NormalDensity(y2-standardDeviation);
		switch(par){
		    case SPOT:
			    d_pw1 = -(2*mu+1)/Spot*pw1;
			    dd_pw1 = 2.*(mu+1)*(2*mu+1)/(Spot*Spot)*pw1;
			    ddd_pw1 = -(2*mu+3)*2.*(mu+1)*(2*mu+1)/(Spot*Spot*Spot)*pw1;
			    
			    d_pw2 = -2*mu/Spot*pw2;
			    dd_pw2 = (2*mu+1)*2*mu/(Spot*Spot)*pw2;
			    ddd_pw2 = -2*(mu+1)*(2*mu+1)*2*mu/(Spot*Spot*Spot)*pw2;
			    
			    d_y2 = -1/(Spot*standardDeviation);
		    	dd_y2 = +1/(Spot*Spot*standardDeviation);
		    	ddd_y2 = -2/(Spot*Spot*Spot*standardDeviation);
		    	
		    	d_N1 = n1*eta*d_y2;
		    	dd_N1 = n1*eta*(dd_y2 - (y2)*d_y2*d_y2);
		    	ddd_N1 = n1*eta*(ddd_y2 - 3*dd_y2*d_y2*(y2)
		    					- d_y2*d_y2*d_y2 + d_y2*d_y2*d_y2*(y2)*(y2) );
		    	
		    	d_N2 = n2*eta*d_y2;
		    	dd_N2 = n2*eta*(dd_y2 - (y2-standardDeviation)*d_y2*d_y2);
		    	ddd_N2 = n2*eta*(ddd_y2 - 3*dd_y2*d_y2*(y2-standardDeviation)
		    					- d_y2*d_y2*d_y2 + d_y2*d_y2*d_y2*(y2-standardDeviation)*(y2-standardDeviation));
		    	switch(ord){
		    	case 1:
		    	//checked
		    		der = -(2.*mu+1.)*psi*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))
		    		*Normals.CumulativeNormal(eta*y2)
		    		
		    		+ 2.*mu*psi*Strike/Spot*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
		    		*Normals.CumulativeNormal(eta*y2-eta*standardDeviation)
		    		
		 		  + psi*eta/standardDeviation*exp((d-r)*Expiry)
		   		  *Normals.NormalDensity(y2)
		    	  *pow(Barrier/Spot,2.*(mu+1))*(Strike/Barrier-1);
		    		break;
		    	case 2:
		    		
		    		der = 2*(mu+1)/Spot*(2.*mu+1.)*psi*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))
				    		*Normals.CumulativeNormal(eta*y2)
				    	
				    	-(2.*mu+1.)*psi*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))
		    		*Normals.NormalDensity(y2)*eta/(-Spot*standardDeviation)
		    		
		    		- (2*mu)*2.*mu*psi*Strike/(Spot*Spot)*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
		    		*Normals.CumulativeNormal(eta*y2-eta*standardDeviation)
		    		
		    		+ 2.*mu*psi*Strike/Spot*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
		    		*Normals.NormalDensity(y2-standardDeviation)*eta/(-Spot*standardDeviation)
		    		
		    			  -2*(mu+1)/Spot*psi*eta/standardDeviation*exp((d-r)*Expiry)*(Strike/Barrier-1)
		    			  	*pow(Barrier/Spot,2.*(mu+1))*Normals.NormalDensity(y2)
		    			  	
		    			  + psi*eta/standardDeviation*exp((d-r)*Expiry)*(Strike/Barrier-1)
		    			  	*pow(Barrier/Spot,2.*(mu+1))*Normals.NormalDensity(y2)*(-y2)/(-Spot*standardDeviation);	
		    			  	;
		    		
		    		break;
		    	case 3:
		    		
		    		der = psi*exp((d-r)*Expiry)*(pw1*ddd_N1 + N1*ddd_pw1 + 3*d_pw1*dd_N1 + 3*dd_pw1*d_N1)
			    			- psi*Strike*exp(-r*Expiry)*(pw2*ddd_N2 + N2*ddd_pw2 + 3*d_pw2*dd_N2 + 3*dd_pw2*d_N2);
			    	break;
		    	default:
		    
		    		break;
		    	}
		    	break;
		
		    case EXPIRY:
		    	//sensitivity to time tau = T-t: derive respect T and change sign
		    	der =  -(d-r)*psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))*Normals.CumulativeNormal(eta*y2)
		    			+ psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))*Normals.NormalDensity(y2)*eta*DerY2(par)
		    			
		    			- r*psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
				*Normals.CumulativeNormal(eta*y2-eta*standardDeviation)
					- psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
				*Normals.NormalDensity(y2-standardDeviation)*eta*(DerY2(par)+0.5*standardDeviation/Expiry);
		    	break;
		    
		    case VOLATILITY:
		    	double d_mu = -2*d/(Vol*Vol*Vol);
		    	der = + log(Barrier/Spot)*2*d_mu*psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))*Normals.CumulativeNormal(eta*y2)
		    		+ psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))
		    		*Normals.NormalDensity(y2)*eta*DerY2(par)
		    		- log(Barrier/Spot)*2*d_mu*psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
				*Normals.CumulativeNormal(eta*y2-eta*standardDeviation)
		    					- psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
				*Normals.NormalDensity(y2-standardDeviation)*eta*(DerY2(par)-sqrt(Expiry));
		    			
		    	break;
		    	
		    case RATE:
		    	der =psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))*log(Barrier/Spot)*(2/(Vol*Vol))
    			*Normals.CumulativeNormal(eta*y2)
    		+psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))
			*Normals.NormalDensity(y2)*eta*standardDeviation/(Vol*Vol)	
		
		+ Expiry*psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
		*Normals.CumulativeNormal(eta*y2-eta*standardDeviation)
    	- psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)*log(Barrier/Spot)*(2/(Vol*Vol))
    	*Normals.CumulativeNormal(eta*y2-eta*standardDeviation)
    	- psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
    	*Normals.NormalDensity(y2-standardDeviation)*eta*standardDeviation/(Vol*Vol);	
		    	break;	
		    	
			default:
				//TODO
			//throw error!!
			break;
		}
		
		return der;
	}	
	
	protected double DerE(int ord, EuropeanBarrierParams par, int eta){
		double der = 0.0;
		double Nx2 = Normals.CumulativeNormal(eta*x2 - eta*standardDeviation);
		double Ny2 = Normals.CumulativeNormal(eta*y2 - eta*standardDeviation);
		double pw = pow(Barrier/Spot,2.*mu);
		double d_x2, dd_x2, d_y2, dd_y2, d_pw, dd_pw, ddd_pw,
		d_Nx2, dd_Nx2, ddd_Nx2, d_Ny2, dd_Ny2, ddd_Ny2, ddd_x2,ddd_y2;
		
		switch(par){
		    case SPOT:
		    	d_pw = -2*mu/Spot*pw;
		    	dd_pw = +(2*mu+1)*2*mu/(Spot*Spot)*pw;
		    	ddd_pw = - (2*mu+2)*(2*mu+1)*2*mu/(Spot*Spot*Spot)*pw;
		    	d_x2 = 1/(Spot*standardDeviation);
		    	dd_x2 = -1/(Spot*Spot*standardDeviation);
		    	ddd_x2 = 2/(Spot*Spot*Spot*standardDeviation);
		    	d_y2 = -1/(Spot*standardDeviation);
		    	dd_y2 = +1/(Spot*Spot*standardDeviation);
		    	ddd_y2 = -2/(Spot*Spot*Spot*standardDeviation);
		    	
		    	d_Nx2 = Normals.NormalDensity(x2-standardDeviation)*eta*d_x2;
		    	dd_Nx2 = Normals.NormalDensity(x2-standardDeviation)*eta*(dd_x2 - (x2-standardDeviation)*d_x2*d_x2);
		    	ddd_Nx2 = Normals.NormalDensity(x2-standardDeviation)*eta
		    			*(ddd_x2 - 3*dd_x2*d_x2*(x2-standardDeviation)
		    					- d_x2*d_x2*d_x2 + d_x2*d_x2*d_x2*(x2-standardDeviation)*(x2-standardDeviation) );
		    	
		    	d_Ny2 = Normals.NormalDensity(y2-standardDeviation)*eta*d_y2;
		    	dd_Ny2 = Normals.NormalDensity(y2-standardDeviation)*eta*(dd_y2 - (y2-standardDeviation)*d_y2*d_y2);
		    	ddd_Ny2 = Normals.NormalDensity(y2-standardDeviation)*eta
		    			*(ddd_y2 - 3*dd_y2*d_y2*(y2-standardDeviation)
		    					- d_y2*d_y2*d_y2 + d_y2*d_y2*d_y2*(y2-standardDeviation)*(y2-standardDeviation) );
		    	
		    	switch(ord){
		    	case 1:
		    		der = 2.*mu*Rebate/Spot*pow(Barrier/Spot,2.*mu)*exp(-r*Expiry)
		    			*Normals.CumulativeNormal(eta*y2-eta*standardDeviation)
		    			
		    			+ 2.*eta*Rebate/Spot/standardDeviation*exp(-r*Expiry)
		    			*Normals.NormalDensity(x2-standardDeviation);
		    		break;
		    	case 2:
		    		der = -(2*mu+1)*2.*mu*Rebate/(Spot*Spot)*pow(Barrier/Spot,2.*mu)*exp(-r*Expiry)
			    			*Normals.CumulativeNormal(eta*y2-eta*standardDeviation)
			    			
			    		+2.*mu*Rebate/Spot*pow(Barrier/Spot,2.*mu)*exp(-r*Expiry)
		    			*Normals.NormalDensity(y2-standardDeviation)*eta/(-Spot*standardDeviation)
		    			
		    			-2.*eta*Rebate/(Spot*Spot)/standardDeviation*exp(-r*Expiry)
		    			*Normals.NormalDensity(x2-standardDeviation)
		    			
		    			-2.*eta*Rebate/Spot/standardDeviation*exp(-r*Expiry)
		    			*Normals.NormalDensity(x2-standardDeviation)*(x2-standardDeviation)/(Spot*standardDeviation);
		    		
		    		
		    		break;
		    	case 3:
		    		der = Rebate*exp(-r*Expiry)*(
		    				ddd_Nx2
		    				-(pw*ddd_Ny2 + Ny2*ddd_pw + 3*d_pw*dd_Ny2 + 3*dd_pw*d_Ny2)
		    				);
		    		
		    		break;
		    	default:
		    		//TODO: 
		    		break;
		    	}
		    	break;
		
		    case EXPIRY:
		    	//sensitivity to time tau = T-t: derive respect T and change sign	
		    	double derPar = Normals.NormalDensity(x2 - standardDeviation)
		    					*eta*(DerX2(par)+0.5*standardDeviation/Expiry)
		    					
		    					- pow(Barrier/Spot,2.*mu)*Normals.NormalDensity(y2-standardDeviation)
		    					*eta*(DerY2(par)+0.5*standardDeviation/Expiry); 
		    	
		    	der = Rebate*exp(-r*Expiry)*derPar
		    			+r* Rebate*exp(-r*Expiry)*(Normals.CumulativeNormal(eta*x2 - eta*standardDeviation) 
						- pow(Barrier/Spot,2.*mu)*Normals.CumulativeNormal(eta*y2-eta*standardDeviation));
		    	break;
		    	
		    case VOLATILITY:
				double d_mu = -2*d/(Vol*Vol*Vol);
				der = Rebate*exp(-r*Expiry)*Normals.NormalDensity(x2-standardDeviation)*eta*(DerX2(par)-sqrt(Expiry)) 
						
						- log(Barrier/Spot)*2*d_mu*Rebate*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
						*Normals.CumulativeNormal(eta*y2-eta*standardDeviation)
						
						- Rebate*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
						*Normals.NormalDensity(y2-standardDeviation)*eta*(DerY2(par)-sqrt(Expiry));
				
		    	break;
		    	
		    case RATE:	
		    	der = -Expiry*Rebate*exp(-r*Expiry)*Normals.CumulativeNormal(eta*x2 - eta*standardDeviation)
		    			+ Rebate*exp(-r*Expiry)*Normals.NormalDensity(x2 - standardDeviation)*eta*standardDeviation/(Vol*Vol)
		    			+Expiry*Rebate*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)*Normals.CumulativeNormal(eta*y2-eta*standardDeviation)
		    			- log(Barrier/Spot)*(2/Vol/Vol)*Rebate*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)*Normals.CumulativeNormal(eta*y2-eta*standardDeviation)
		    			- Rebate*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)*Normals.NormalDensity(y2-standardDeviation)*eta*standardDeviation/(Vol*Vol);
		    	break;	
		    	
			default:
				//TODO
			//throw error!!
			break;
		}
		
		return der;
	}	
	
	protected double DerF(int ord, EuropeanBarrierParams par, int eta){
		double d_lambda = 0.0;
		double d_mu = 0.0;
		double der = 0.0;
		double d_pow1,d_pow2,dd_pow1,dd_pow2,ddd_pow1,ddd_pow2,d_N1,d_N2,dd_N1,dd_N2,d_z,dd_z,ddd_z,ddd_N1,ddd_N2;
		double pow1 = pow(Barrier/Spot,mu+lambda);
		double pow2 = pow(Barrier/Spot,mu-lambda);
		double N1 = Normals.CumulativeNormal(eta*z);
		double N2 = Normals.CumulativeNormal(eta*z-2.*eta*lambda*standardDeviation);
		switch(par){
		    case SPOT:
		    	d_z = -1/(Spot*standardDeviation);
		    	dd_z = +1/(Spot*Spot*standardDeviation);
		    	ddd_z = -2/(Spot*Spot*Spot*standardDeviation);
		    	
	    		d_pow1 = -(mu+lambda)/Spot*pow(Barrier/Spot,mu+lambda);
	    		dd_pow1= +(mu+lambda+1)/Spot*(mu+lambda)/Spot*pow(Barrier/Spot,mu+lambda);
	    		ddd_pow1 = -(mu+lambda+2)/Spot*(mu+lambda+1)/Spot*(mu+lambda)/Spot*pow1;
	    		d_N1=Normals.NormalDensity(z)*eta*d_z;
	    		
	    		dd_N1=-Normals.NormalDensity(z)*eta
	    				*(log(Barrier/Spot)+(lambda-1)*pow(standardDeviation,2))
	    				/(Spot*Spot*pow(standardDeviation,3));
	    		
	    		ddd_N2 = Normals.NormalDensity(z-2.*lambda*standardDeviation)*eta
		    			*(ddd_z - 3*dd_z*d_z*(z-2.*lambda*standardDeviation)
		    					- d_z*d_z*d_z + d_z*d_z*d_z*(z-2.*lambda*standardDeviation)*(z-2.*lambda*standardDeviation) );
	    		ddd_N1 = Normals.NormalDensity(z)*eta
		    			*(ddd_z - 3*dd_z*d_z*(z)
		    					- d_z*d_z*d_z + d_z*d_z*d_z*(z)*(z) );
	    		
	    		d_pow2 =-(mu-lambda)/Spot*pow(Barrier/Spot,mu-lambda);
	    		dd_pow2=+(mu-lambda+1)/Spot*(mu-lambda)/Spot*pow(Barrier/Spot,mu-lambda);
	    		ddd_pow2 = -(mu-lambda+2)*(mu-lambda+1)*(mu-lambda)*pow2/(Spot*Spot*Spot);
	    		d_N2=Normals.NormalDensity(z-2.*lambda*standardDeviation)*eta*d_z;
	    		
	    		dd_N2=-Normals.NormalDensity(z-2.*lambda*standardDeviation)*eta
	    				*(log(Barrier/Spot)-(lambda+1)*pow(standardDeviation,2))
	    				/(Spot*Spot*pow(standardDeviation,3));
		    	switch(ord){
		    	case 1:
		    		
		    		der = Rebate*(d_pow1*Normals.CumulativeNormal(eta*z)
		    					+ pow(Barrier/Spot,mu+lambda)*d_N1
		    					
		    					+d_pow2*Normals.CumulativeNormal(eta*z-2.*eta*lambda*standardDeviation)
		    					+pow(Barrier/Spot,mu-lambda)*d_N2
		    				);
	
		    		break;
		    	case 2:
		//TODO    		//check it...		
		    		der = Rebate*(
		    				dd_pow1*Normals.CumulativeNormal(eta*z)
		    				+ dd_N1*pow(Barrier/Spot,mu+lambda)
		    				+2*d_N1*d_pow1
		    				
		    				+dd_pow2*Normals.CumulativeNormal(eta*z-2.*eta*lambda*standardDeviation)
		    				+ dd_N2*pow(Barrier/Spot,mu-lambda)
		    				+2*d_N2*d_pow2
		    				);
		    		break;
		    	case 3:
		    		der = Rebate*(
		    				(pow1*ddd_N1 + N1*ddd_pow1 + 3*d_pow1*dd_N1 + 3*dd_pow1*d_N1)
		    				+(pow2*ddd_N2 + N2*ddd_pow2 + 3*d_pow2*dd_N2 + 3*dd_pow2*d_N2)
		    				);
		    		break;
		    	default:
		    		//TODO: 
		    		break;
		    	}
		    	break;
		    	
		    case EXPIRY:
		    	//sensitivity to time tau = T-t: derive respect T and change sign		
		    	der = Rebate*pow(Barrier/Spot,mu+lambda)*Normals.NormalDensity(eta*z)*eta*DerZ(par)
		    		+ 	Rebate*pow(Barrier/Spot,mu-lambda)
		    		*Normals.NormalDensity(z-2.*lambda*standardDeviation)*eta*(DerZ(par)+lambda*standardDeviation/Expiry);
		    	break;
		    
		    case VOLATILITY:
		    	d_mu = -2*d/(Vol*Vol*Vol);
		    	d_lambda = 0.5*(2*mu*d_mu - 4*r/(Vol*Vol*Vol))/lambda;
		    	
					der = Rebate*log(Barrier/Spot)*(d_mu+d_lambda)*pow(Barrier/Spot,mu+lambda)
		    				*Normals.CumulativeNormal(eta*z)
		    		+ Rebate*pow(Barrier/Spot,mu+lambda)*Normals.NormalDensity(z)*eta*DerZ(par)
		    		
		    		+Rebate*log(Barrier/Spot)*(d_mu-d_lambda)*pow(Barrier/Spot,mu-lambda)
		    		*Normals.CumulativeNormal(eta*z-2.*eta*lambda*standardDeviation)
		    		+Rebate*pow(Barrier/Spot,mu-lambda)
		    		*Normals.NormalDensity(z-2.*lambda*standardDeviation)*eta*(DerZ(par)-2*d_lambda*standardDeviation
		    				-2*lambda*sqrt(Expiry));		    	
		    	break;
		    	
		    case RATE:
		    	d_lambda = 1/(lambda*Vol*Vol)*(mu+1);
		    	d_z = standardDeviation*d_lambda;		    	
		    	d_mu = 1/(Vol*Vol);
				der = log(Barrier/Spot)*(d_mu+d_lambda)*pow(Barrier/Spot,mu+lambda)
						*Normals.CumulativeNormal(eta*z)
						
					+ pow(Barrier/Spot,mu+lambda)*Normals.NormalDensity(z)*eta*d_z
					
				
					+log(Barrier/Spot)*(d_mu-d_lambda)*pow(Barrier/Spot,mu-lambda)
					*Normals.CumulativeNormal(eta*z-2.*eta*lambda*standardDeviation)
					
					+pow(Barrier/Spot,mu-lambda)*Normals.NormalDensity(z-2.*lambda*standardDeviation)
					*eta*(d_z-2*standardDeviation*d_lambda);
				;
				der = Rebate*der;
				//TODO
				break;	
		
			default:
				//TODO
			//throw error!!
			break;
		}
		
		return der;
	}	
	
	protected double Spot;
	protected double Strike;
	private double r;
	private double d;
	private double Expiry;
	private double Vol;
	protected double Barrier;
	private double Rebate;
	
	public static enum EuropeanBarrierParams {SPOT, STRIKE, RATE, DIVIDEND, VOLATILITY, EXPIRY, BARRIER, REBATE};
	public static final String[] EuropeanParameters ={"SPOT","STRIKE","RATE",
		"DIVIDEND","VOLATILITY","EXPIRY","BARRIER","REBATE"};
	
	private double standardDeviation, x1,x2,y1,y2,z,mu,lambda;
	
}
