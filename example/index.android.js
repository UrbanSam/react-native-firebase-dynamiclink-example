/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  ToastAndroid
} from 'react-native';

import EventEmitter from 'EventEmitter';

import FDL, { FDLEvent } from './scripts/RNFIRDynamicLink';

export default class FIRDL extends Component {
	
    constructor () 
    {
        super()
		
		//1. Initialize Firebase Dynamiclink
        FDL.authorize();
		
		//2. Create a listener for dynamic link, make sure to remove it when component unmount
		//Listener should only create once only
		this.dynamicLinkListener = FDL.on(FDLEvent.DynamicLink, dynamiclink =>{

			if(Platform.OS === 'ios')
			{
				//TODO
				//Implement for IOS incoming
			}
			else
			{
				ToastAndroid.show('Received Real Dynamic Link ! \n' + dynamiclink.url, ToastAndroid.LONG);
			}
			
			//Dynamic Link data handling
			this.DynamicLinkDataHandler(dynamiclink);

		});
		
		//Fake data example, this is the formal we'll received
		const exampleData = 
		{
			url:"http://APP_REFERRAL/0123456/DeepLink",
			invitationId:"0123456",
		}
		
		this.DynamicLinkDataHandler(exampleData);
		//END of Fake data example
    }
	
	componentWillUnmount()
	{
		//Remove listener to prevent leaking
		this.dynamicLinkListener.remove();
	}

	//Some data handling, you could customize your own handler
	DynamicLinkDataHandler(data)
	{
		try
		{
			const url = data.url;
			console.log("Received DYNAMIC LINK : "+url);

			//This is if your app referral code is in this format
			//e.g http://APP_REFERRAL/0123456/DeepLink
			
			var splitString = url.split('//');
			splitString = splitString[1].split('/');
			const action = splitString[0];
			const params = splitString[1];

			switch(action)
			{	
				case 'APP_REFERRAL':
				case 'app_referral':
					//Do some stuff
					console.log("Hey you just got a referral!");
				break;
			}

		}
		catch(e)
		{
			console.warn('Url not found for dynamic link: '+e+'\n'+JSON.stringify(data));
			return;
		}
	}
	
    render() 
    {
        return (
            <View style={styles.container}>
                <Text style={styles.welcome}>
                    Welcome to React Native!
                </Text>
                <Text style={styles.instructions}>
                    To get started, edit index.android.js
                </Text>
                <Text style={styles.instructions}>
                    Double tap R on your keyboard to reload,{'\n'}
                    Shake or press menu button for dev menu
                </Text>
            </View>
        );
    }
}


const styles = StyleSheet.create({
    container: 
    {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
    },
    welcome: 
    {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
    },
    instructions: 
    {
        textAlign: 'center',
        color: '#333333',
        marginBottom: 5,
    },
});

AppRegistry.registerComponent('FIRDL', () => FIRDL);
