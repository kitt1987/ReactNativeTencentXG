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
    devToken: 'waiting',
  };


  componentDidMount() {
    XG.enableDebug(true);
    // Your accessId as number and your accessKey
    const accessIdInNumber = 2100182505;// 0;
    const accessKey = 'A5J92KR9D8IE';//'Your access key';
    XG.setCredential(accessIdInNumber, accessKey);
    XG.register('SampleTester')
      .then(devToken => {
        console.log(devToken);
        this.setState({devToken});
      })
      .catch(err => {
        this.setState({devToken: err});
      });
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.instructions}>
          {'Your device token is' + this.state.devToken}
        </Text>
        <Text style={styles.instructions}
          onPress={
            () => {
              XG.sendLocalNotification('title', 'content',
                Date.now() + 5000);
              BackAndroid.exitApp();
            }
          }>
          Press to send a local notification after 5s
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
