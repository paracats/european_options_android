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
		d = d_;
		Expiry = T_;
		Vol = vol_;
		Barrier = Barrier_;
		Rebate = Rebate_;
		//pre-compute these
		standardDeviation = Vol*sqrt(Expiry);

		mu = d/(Vol*Vol)-0.5;
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
		    		break;
		    	default:
		    		//TODO: 
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
		    	
		    	break;
		    	
		    case RATE:
		    	
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
		switch(par){
		    case SPOT:
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
		    	
		    	break;
		    	
		    case RATE:
		    	
		    	break;
		
			default:
				//TODO
			//throw error!!
			break;
		}
		
		return der;
	}	
	
	protected double DerC(int ord, EuropeanBarrierParams par, int psi, int eta){
		double der = 0.0;
		switch(par){
		    case SPOT:
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
		    		break;
		    	default:
		    		//TODO: 
		    		break;
		    	}
		    	break;
		
		    case EXPIRY:
		    	//sensitivity to time tau = T-t: derive respect T and change sign	
		    	der = -(d-r)*psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))*Normals.CumulativeNormal(eta*y1)
		    			+ psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))
		    			*Normals.NormalDensity(y1)*eta*DerY1(par)
		    			
		    			- r*psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)*Normals.CumulativeNormal(eta*y1-eta*standardDeviation)
		    	- psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
		    	*Normals.NormalDensity(y1-standardDeviation)*(DerY1(par)+0.5*standardDeviation/Expiry);
		    	
		    	break;
		    
		    case VOLATILITY:
		    	
		    	break;
		    	
		    case RATE:
		    	
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
		switch(par){
		    case SPOT:
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
		    		break;
		    	default:
		    		//TODO: 
		    		break;
		    	}
		    	break;
		
		    case EXPIRY:
		    	//sensitivity to time tau = T-t: derive respect T and change sign
		    	der =  -(d-r)*psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))*Normals.CumulativeNormal(eta*y2)
		    			+ psi*Spot*exp((d-r)*Expiry)*pow(Barrier/Spot,2.*(mu+1))*Normals.NormalDensity(eta*y2)*eta*DerY2(par)
		    			
		    			- r*psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
				*Normals.CumulativeNormal(eta*y2-eta*standardDeviation)
					- psi*Strike*exp(-r*Expiry)*pow(Barrier/Spot,2.*mu)
				*Normals.NormalDensity(y2-standardDeviation)*eta*(DerY2(par)+0.5*standardDeviation/Expiry);
		    	break;
		    
		    case VOLATILITY:
		    	
		    	break;
		    	
		    case RATE:
		    	
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
		switch(par){
		    case SPOT:
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
		    	
		    	break;
		    	
		    case RATE:
		    	
		    	break;	
		    	
			default:
				//TODO
			//throw error!!
			break;
		}
		
		return der;
	}	
	
	protected double DerF(int ord, EuropeanBarrierParams par, int eta){
		double der = 0.0;
		switch(par){
		    case SPOT:
		    	switch(ord){
		    	case 1:
		    		der =Rebate/Spot
		    			*(-(mu+lambda)*pow(Barrier/Spot,mu+lambda)*Normals.CumulativeNormal(eta*z)
		    			  -(mu-lambda)*pow(Barrier/Spot,mu-lambda)
		    			  *Normals.CumulativeNormal(eta*z-2.*eta*lambda*standardDeviation));
		    		break;
		    	case 2:
		    		der = +(mu+lambda+1)*Rebate/(Spot*Spot)
			    			*(mu+lambda)*pow(Barrier/Spot,mu+lambda)*Normals.CumulativeNormal(eta*z)
			    			
			    			-Rebate/Spot
			    			*(mu+lambda)*pow(Barrier/Spot,mu+lambda)
			    			*Normals.NormalDensity(z)*eta/(-Spot*standardDeviation)
			    			
			    		+(mu-lambda+1)*Rebate/(Spot*Spot)
			    			*(mu-lambda)*pow(Barrier/Spot,mu-lambda)
			    			*Normals.CumulativeNormal(eta*z-2.*eta*lambda*standardDeviation)
			    			
			    			-Rebate/Spot
			    			*(mu-lambda)*pow(Barrier/Spot,mu-lambda)
			    			*Normals.NormalDensity(z-2.*lambda*standardDeviation)*eta/(-Spot*standardDeviation);
		    		
		    		break;
		    	case 3:
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
		    	
		    	break;
		    	
		    case RATE:
		    	
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
	//TODO change name to EurPar
	public static enum EuropeanBarrierParams {SPOT, STRIKE, RATE, DIVIDEND, VOLATILITY, EXPIRY, BARRIER, REBATE};
	public static final String[] EuropeanParameters ={"SPOT","STRIKE","RATE",
		"DIVIDEND","VOLATILITY","EXPIRY","BARRIER","REBATE"};
	
	private double standardDeviation, x1,x2,y1,y2,z,mu,lambda;
	
}
