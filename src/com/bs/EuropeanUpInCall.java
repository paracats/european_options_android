package com.bs;

public class EuropeanUpInCall extends EuropeanBarrierOption implements EuropeanOption{
//default constructor
	public EuropeanUpInCall() {
		super(UpInDefault[0], UpInDefault[1], UpInDefault[2], UpInDefault[3],
				UpInDefault[4], UpInDefault[5], UpInDefault[6], UpInDefault[7]);
	
		//the option is meaningless if Spot>Barrier => convert to a vanilla call??
		if(Spot>=Barrier){
			//EXCEPTION
			throw new IllegalArgumentException("The price has reached the barrier level! "
												+ "Spot should be less than Barrier.");
		}
	}

	public EuropeanUpInCall(double Spot_, double Strike_, double r_, double d_,
			double T_, double vol_, double Barrier_, double Rebate_) {
		super(Spot_, Strike_, r_, d_, T_, vol_, Barrier_, Rebate_);
	
		//the option is meaningless if Spot>Barrier => convert to a vanilla call??
		if(Spot>=Barrier){
			//EXCEPTION
			throw new IllegalArgumentException("The price has reached the barrier level! "
												+ "Spot should be less than Barrier.");
		}
	}

	
	@Override
	public double getPrice() {			
		if(Strike>Barrier)
			return A(+1)+E(-1);
		else
			return B(+1)-C(+1,-1)+D(+1,-1)+E(-1);	
	}

	@Override
	public double getDelta() {
		if(Strike>Barrier)
			return DerA(1,EuropeanBarrierParams.SPOT,1)+DerE(1,EuropeanBarrierParams.SPOT,-1);
		else 
			return DerB(1,EuropeanBarrierParams.SPOT,1)-DerC(1,EuropeanBarrierParams.SPOT,1,-1)
					+DerD(1,EuropeanBarrierParams.SPOT,1,-1)+DerE(1,EuropeanBarrierParams.SPOT,-1);	
	}

	@Override
	public double getGamma() {
		if(Strike>Barrier)
			return DerA(2,EuropeanBarrierParams.SPOT,1)+DerE(2,EuropeanBarrierParams.SPOT,-1);
		else 
			return DerB(2,EuropeanBarrierParams.SPOT,1)-DerC(2,EuropeanBarrierParams.SPOT,1,-1)
					+DerD(2,EuropeanBarrierParams.SPOT,1,-1)+DerE(2,EuropeanBarrierParams.SPOT,-1);	
	}

	@Override
	public double getTheta() {
		if(Strike>Barrier)
			return DerA(1,EuropeanBarrierParams.EXPIRY,1)+DerE(1,EuropeanBarrierParams.EXPIRY,-1);
		else 
			return DerB(1,EuropeanBarrierParams.EXPIRY,1)-DerC(1,EuropeanBarrierParams.EXPIRY,1,-1)
					+DerD(1,EuropeanBarrierParams.EXPIRY,1,-1)+DerE(1,EuropeanBarrierParams.EXPIRY,-1);	
	}

	@Override
	public double getSpeed() {
		if(Strike>Barrier)
			return DerA(3,EuropeanBarrierParams.SPOT,1)+DerE(3,EuropeanBarrierParams.SPOT,-1);
		else 
			return DerB(3,EuropeanBarrierParams.SPOT,1)-DerC(3,EuropeanBarrierParams.SPOT,1,-1)
					+DerD(3,EuropeanBarrierParams.SPOT,1,-1)+DerE(3,EuropeanBarrierParams.SPOT,-1);	
	}

	@Override
	public double getVega() {
		if(Strike>Barrier)
			return DerA(1,EuropeanBarrierParams.VOLATILITY,1)+DerE(1,EuropeanBarrierParams.VOLATILITY,-1);
		else 
			return DerB(1,EuropeanBarrierParams.VOLATILITY,1)-DerC(1,EuropeanBarrierParams.VOLATILITY,1,-1)
					+DerD(1,EuropeanBarrierParams.VOLATILITY,1,-1)+DerE(1,EuropeanBarrierParams.VOLATILITY,-1);
	}

	@Override
	public double getRho() {
		if(Strike>Barrier)
			return DerA(1,EuropeanBarrierParams.RATE,1)+DerE(1,EuropeanBarrierParams.RATE,-1);
		else 
			return DerB(1,EuropeanBarrierParams.RATE,1)-DerC(1,EuropeanBarrierParams.RATE,1,-1)
					+DerD(1,EuropeanBarrierParams.RATE,1,-1)+DerE(1,EuropeanBarrierParams.RATE,-1);
	}

public static double[] UpInDefault = {100.,110.,0.08,0.04,0.25,0.5,105.,3.};	
	
}
