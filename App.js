import React from 'react';
//import DeviceEventEmitter and NativeModules for communication with native SDK
import {  DeviceEventEmitter, NativeModules, StyleSheet, FlatList, Text, View, Alert } from 'react-native';

var lockListData =[];
///init NativeModule with module name
const PartnerSDKModule = NativeModules.PartnerSDKModule;
export default class App extends React.Component {
  componentDidMount() {
    ///use startScan to start bluetooth scan
    PartnerSDKModule.startScan((error) => {
      console.log(error);
    });
    ///listen to bluetooth scan result
    ///use event name to listen to spesific event, in this example we use 'scanLock' as the event name
      this.listener = DeviceEventEmitter.addListener('scanLock', e => {
        const index = lockListData.findIndex((i) => i.name === e.name);
        if (index === -1) {
            lockListData.push(e);
        } else {
            lockListData[index] = e;
        }
        this.setState ({  
          lockList: lockListData
        })
      }
    )
  }
  componentWillUnmount(){
    PartnerSDKModule.stopScan();
  }
  constructor(props) {
    super(props);
    this.state = {
      lockList: lockListData
    };
  }
  FlatListItemSeparator = () => {
    return (
      //Item Separator
      <View style={{height: 0.5, width: '100%', backgroundColor: '#C8C8C8'}}/>
    );
  };
  GetItem(item) {
    //Function for click on an item
    Alert.alert(item);
  }
  render() {
    return (
      <View style={styles.MainContainer}>
        <FlatList
          data={this.state.lockList}
          extraData={this.state}
          //data defined in constructor
          ItemSeparatorComponent={this.FlatListItemSeparator}
          //Item Separator View
          renderItem={({ item }) => (
            // Single Comes here which will be repeatative for the FlatListItems
            <View>
              <Text
                style={item.isActive? styles.itemActive: styles.itemInActive}
                onPress={this.GetItem.bind(this, 'name : '+item.id+' Value : '+item.value)}>
                {item.name}
              </Text>
            </View>
          )}
          keyExtractor={(item, index) => index.toString()}
        />
      </View>
    );
  }
}
const styles = StyleSheet.create({
  MainContainer: {
    justifyContent: 'center',
    flex: 1,
    marginLeft: 10,
    marginRight: 10,
    marginBottom: 10,
    marginTop: 30,
  },
 
  itemInActive: {
    padding: 10,
    color: '#000000',
    fontSize: 18,
    height: 44,
  },
  itemActive: {
    padding: 10,
    color: '#9C27B0',
    fontSize: 18,
    height: 44,
  },
});