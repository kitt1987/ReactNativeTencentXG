/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */

import React, {
  Alert,
  AppRegistry,
  Component,
  BackAndroid,
  StyleSheet,
  Text,
  View
} from 'react-native';

import * as XG from 'react-native-tencent-xg';
import * as Remote from './remote';

class sample extends Component {
  state = {
    event: '',
    eventArgs: null,
    badgeNum: 0,
    localUserInfo: {id: 1},
    devToken: '',
  };

  componentDidMount() {
    XG.enableDebug(true);
    console.log(XG.allEvents());
    var registerHolder = XG.addEventListener('register', devToken => {
      this.setState({
        event: 'register',
        eventArgs: JSON.stringify(devToken),
        devToken,
      });
    });

    if (!registerHolder) console.log('Fail to add event to handle register');

    var errorHolder = XG.addEventListener('error', err => {
      this.setState({
        event: 'error',
        eventArgs: JSON.stringify(err)
      });
    });

    if (!errorHolder) console.log('Fail to add event to handle error');

    var remoteHolder = XG.addEventListener('notification', xgInstance => {
      this.setState({
        event: 'notification',
        eventArgs: JSON.stringify(xgInstance)
      });
    });

    if (!remoteHolder)
      console.log('Fail to add event to handle remote notification');

    var localHolder = XG.addEventListener('localNotification', xgInstance => {
      console.log(xgInstance);
      this.setState({
        event: 'localNotification',
        eventArgs: JSON.stringify(xgInstance)
      });
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
    XG.getApplicationIconBadgeNumber()
      .then(badgeNum => {
        this.setState({badgeNum});
      })
  }

  componentWillUnmount() {
    this.state.eventsHolder.filter(h => !!h).forEach(holder => holder.remove());
  }

  render() {
    return (
      <View style={styles.container}>
        <View style={styles.row}>
          <Text style={styles.instructions}>Event</Text>
          <Text style={[styles.instructions, {color: 'red'}]}>
            {'[' + this.state.event + ']'}
          </Text>
        </View>
        <Text style={styles.instructions}>
          {this.state.eventArgs}
        </Text>
        <View style={styles.row}>
          <Text style={styles.instructions}>Badge</Text>
          <Text style={[styles.instructions, {color: 'red'}]}>
            {'[' + this.state.badgeNum + ']'}
          </Text>
          <Text style={[styles.instructions, styles.button]}
            onPress={() => {
              this.setState({badgeNum: this.state.badgeNum + 1},
                () => XG.setApplicationIconBadgeNumber(this.state.badgeNum));
              Alert.alert(`This makes no sense on Android`);
            }}>
            Plus 1
          </Text>
          <Text style={[styles.instructions, styles.button]}
            onPress={() => {
              this.setState({badgeNum: 0}),
              XG.setApplicationIconBadgeNumber(0);
              Alert.alert(`This makes no sense on Android`);
            }}
          >
            Clear
          </Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.instructions}>Local Notification</Text>
          <Text style={[styles.instructions, styles.button]}
            onPress={() => {
              var fireDate = Date.now() + 60000;
              XG.scheduleLocalNotification({
                fireDate,
                alertBody: 'content of ' + fireDate,
                userInfo: this.state.localUserInfo
              });
              Alert.alert(`The notification will trigger after 1min. You have to
                go back to desktop`);
            }}
          >
            Send
          </Text>
          <Text style={[styles.instructions, styles.button]}
            onPress={() => {
              XG.cancelLocalNotifications(this.state.localUserInfo);
              Alert.alert(`Cancelled a notification`);
            }}
          >
            Cancel
          </Text>
          <Text style={[styles.instructions, styles.button]}
            onPress={() => {
              XG.cancelAllLocalNotifications();
              Alert.alert(`Clear all notifications`);
            }}
          >
            Clear
          </Text>
        </View>
        <View style={styles.row}>
          <Text style={styles.instructions}>Remote Notification</Text>
          <Text style={[styles.instructions, styles.button]}
            onPress={() => {
              Remote.push(this.state.devToken, 'Send a remote testing message',
                'Testing');
                Alert.alert(`The notification will trigger after 5s. You have to
                  go back to desktop`);
            }}>
            Request
          </Text>
          <Text style={[styles.instructions, styles.button]}
            onPress={() => {
              Remote.broadcast('Send a remote testing broadcast', 'Testing');
              Alert.alert(`The notification will trigger after 5s. You have to
                go back to desktop`);
            }}
          >
            Broadcast
          </Text>
        </View>
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
    fontSize: 15,
    marginBottom: 5,
    marginHorizontal: 5
  },
  button: {
    paddingVertical: 5,
    paddingHorizontal: 10,
    borderWidth: 1,
    borderRadius: 5,
    borderColor: '#333333'
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-around'
  }
});

AppRegistry.registerComponent('sample', () => sample);
