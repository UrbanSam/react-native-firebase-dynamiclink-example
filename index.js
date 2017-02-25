'use strict';

import {
	NativeModules,
	DeviceEventEmitter,
} from 'react-native';

export const FDLEvent = {
  DynamicLink: 'FIRDynamicLinkReceived',
}

const RNFIRDynamicLink = NativeModules.RNFIRDynamicLink;

const FDL = {};

FDL.authorize =  () => {
    RNFIRDynamicLink.authorize();
}

FDL.getDynamicLink =  () => {
    RNFIRDynamicLink.getDynamicLink();
}

FDL.on = (event, callback) => {
    if (!Object.values(FDLEvent).includes(event)) {
        throw new Error(`Invalid FDL event subscription, use import {FDLEvent} avoid typo`);
    };
    
    return DeviceEventEmitter.addListener(event, callback);
};

export default FDL;
