/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */

import React, {
  AppRegistry,
  Component,
  BackAndroid,
  StyleSheet,
  Text,
  View
} from 'react-native';

import * as XG from 'react-native-tencent-xg';

class sample extends Component {
  state = {
    devToken: 'waiting for registration',
  };

  componentDidMount() {
    XG.enableDebug(true);
    console.log(XG.allEvents());
    var registerHolder = XG.addEventListener('register', devToken => {
      console.log('Got devToken:' + devToken);
      this.setState({devToken});
    });

    if (!registerHolder) console.log('Fail to add event to handle register');

    var errorHolder = XG.addEventListener('error', err => {
      console.log('Error issued');
      console.log(err);
    });

    if (!errorHolder) console.log('Fail to add event to handle error');

    var remoteHolder = XG.addEventListener('notification', xgInstance => {
      console.log(xgInstance);
    });

    if (!remoteHolder)
      console.log('Fail to add event to handle remote notification');

    var localHolder = XG.addEventListener('localNotification', xgInstance => {
      console.log(xgInstance);
    });

    if (!localHolder) console.log('Fail to add event to local notification');

    this.setState({
      eventsHolder: [
        registerHolder,
        errorHolder,
        remoteHolder,
        localHolder
      ]
    });

    // Your accessId as number and your accessKey
    XG.setCredential(2100197325, 'A253BZM7P9NF');
    XG.register('SampleTester');
  }

  componentWillUnmount() {
    this.state.eventsHolder.filter(h => !!h).forEach(holder => holder.remove());
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.instructions}>
          {'Your device token is ' + this.state.devToken}
        </Text>
        <Text style={styles.instructions}
          onPress={
            () => {
              XG.scheduleLocalNotification({
                fireDate: Date.now() + 60000,
                alertBody: 'content',
              });
              BackAndroid.exitApp();
            }
          }>
          Press to send a local notification after 1min
        </Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

AppRegistry.registerComponent('sample', () => sample);
