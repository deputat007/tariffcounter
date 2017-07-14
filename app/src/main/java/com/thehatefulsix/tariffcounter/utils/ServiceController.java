package com.thehatefulsix.tariffcounter.utils;


import android.content.Context;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.models.Service;


public class ServiceController {

    public static String serviceTypeGetString(Service.Type type, Context context) {
        String serviceType = null;

        if(type != null) {
            switch(type) {
                case OTHER: serviceType = context.getResources()
                        .getStringArray(R.array.service_type_spinner_array)[1];
                    break;
                case ELECTRICITY_SUPPLY: serviceType = context.getResources()
                        .getStringArray(R.array.service_type_spinner_array)[2];
                    break;
                case GAS_SUPPLY: serviceType = context.getResources()
                        .getStringArray(R.array.service_type_spinner_array)[3];
                    break;
                case RENT: serviceType = context.getResources()
                        .getStringArray(R.array.service_type_spinner_array)[4];
                    break;
                case HEATING: serviceType = context.getResources()
                        .getStringArray(R.array.service_type_spinner_array)[5];
                    break;
                case HOT_WATER: serviceType = context.getResources()
                        .getStringArray(R.array.service_type_spinner_array)[6];
                    break;
                case WATER_SUPPLY: serviceType = context.getResources()
                        .getStringArray(R.array.service_type_spinner_array)[7];
                    break;
                case WATER_DRAINAGE: serviceType = context.getResources()
                        .getStringArray(R.array.service_type_spinner_array)[8];
                    break;
                case GARBAGE_COLLECTION: serviceType = context.getResources()
                        .getStringArray(R.array.service_type_spinner_array)[9];
                    break;
                default: break;
            }
        }

        return serviceType;
    }

    public static String rateUnitGetString(Service.RateUnit unit, Context context) {
        String rateUnit = null;

        if(unit != null) {
            switch(unit) {
                case NULL: rateUnit = "";
                    break;
                case M2: rateUnit = context.getResources()
                        .getStringArray(R.array.rate_unit_spinner_array)[1];
                    break;
                case M3: rateUnit = context.getResources()
                        .getStringArray(R.array.rate_unit_spinner_array)[2];
                    break;
                case KWH: rateUnit = context.getResources()
                        .getStringArray(R.array.rate_unit_spinner_array)[3];
                    break;
                case GCAL: rateUnit = context.getResources()
                        .getStringArray(R.array.rate_unit_spinner_array)[4];
                    break;
                default: break;
            }
        }

        return rateUnit;
    }

    public static Service.Type getServiceTypeFromName (String serviceName, Context context) {
        Service.Type type = null;
        String[] serviceNames = context.getResources()
                .getStringArray(R.array.service_type_spinner_array);
        final String other = serviceNames[1];
        final String electricity = serviceNames[2];
        final String gas = serviceNames[3];
        final String rent = serviceNames[4];
        final String heating = serviceNames[5];
        final String hotWater = serviceNames[6];
        final String water = serviceNames[7];
        final String waterDrainage = serviceNames[8];
        final String garbageCollection = serviceNames[9];

        if (serviceName != null){
            if (serviceName.equals(other)){
                type = Service.Type.OTHER;
            }
            if (serviceName.equals(electricity)){
                type = Service.Type.ELECTRICITY_SUPPLY;
            }
            if (serviceName.equals(gas)){
                type = Service.Type.GAS_SUPPLY;
            }
            if (serviceName.equals(rent)){
                type = Service.Type.RENT;
            }
            if (serviceName.equals(heating)){
                type = Service.Type.HEATING;
            }
            if (serviceName.equals(hotWater)){
                type = Service.Type.HOT_WATER;
            }
            if (serviceName.equals(water)){
                type = Service.Type.WATER_SUPPLY;
            }
            if (serviceName.equals(waterDrainage)){
                type = Service.Type.WATER_DRAINAGE;
            }
            if (serviceName.equals(garbageCollection)){
                type = Service.Type.GARBAGE_COLLECTION;
            }

        }
        return type;
    }
}