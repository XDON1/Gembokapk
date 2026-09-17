import React, { useState, useEffect } from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, FlatList, Alert, StatusBar, SafeAreaView } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as Clipboard from 'expo-clipboard';
export default function App() {
  const [isLocked, setIsLocked] = useState(true);
  const [masterPin, setMasterPin] = useState(null);
  const [inputPin, setInputPin] = useState('');
  const [isDarkMode, setIsDarkMode] = useState(true);
  const [passwords, setPasswords] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [currentScreen, setCurrentScreen] = useState('home');
  const [title, setTitle] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [siteOrApp, setSiteOrApp] = useState('');
  useEffect(() => { loadAppData(); }, []);
  const loadAppData = async () => {
    try {
      const storedPin = await AsyncStorage.getItem('@gembok_pin');
      const storedPasswords = await AsyncStorage.getItem('@gembok_passwords');
      if (storedPin) setMasterPin(storedPin); else setIsLocked(false);
      if (storedPasswords) setPasswords(JSON.parse(storedPasswords));
    } catch (e) { console.error(e); }
  };
  const handleUnlock = async () => {
    if (!masterPin) {
      if (inputPin.length < 4) { Alert.alert('Error', 'PIN minimal 4 karakter'); return; }
      await AsyncStorage.setItem('@gembok_pin', inputPin);
      setMasterPin(inputPin); setIsLocked(false); setInputPin('');
    } else {
      if (inputPin === masterPin) { setIsLocked(false); setInputPin(''); }
      else { Alert.alert('Salah', 'PIN Master tidak valid'); }
    }
  };
  const savePasswordItem = async () => {
    if (!title || !username || !password) { Alert.alert('Error', 'Wajib diisi'); return; }
    const newItem = { id: Date.now().toString(), title, username, password, siteOrApp };
    const updated = [newItem, ...passwords];
    setPasswords(updated);
    await AsyncStorage.setItem('@gembok_passwords', JSON.stringify(updated));
    setTitle(''); setUsername(''); setPassword(''); setSiteOrApp(''); setCurrentScreen('home');
  };
  const deleteItem = async (id) => {
    const updated = passwords.filter(item => item.id !== id);
    setPasswords(updated);
    await AsyncStorage.setItem('@gembok_passwords', JSON.stringify(updated));
  };
  const copyPassword = async (pass) => {
    await Clipboard.setStringAsync(pass);
    Alert.alert('Disalin', 'Otomatis dihapus dalam 1 menit.');
    setTimeout(async () => {
      if (await Clipboard.getStringAsync() === pass) await Clipboard.setStringAsync('');
    }, 60000);
  };
  const getStrength = (p) => {
    if (!p) return {t:'', c:'gray'};
    if (p.length < 6) return {t:'Lemah', c:'#ff4d4d'};
    if (p.length < 10) return {t:'Sedang', c:'#ffa500'};
    return {t:'Kuat', c:'#2ecc71'};
  };
  const str = getStrength(password);
  const filtered = passwords.filter(i => i.title.toLowerCase().includes(searchQuery.toLowerCase()));
  const theme = isDarkMode ? {bg:'#121212', card:'#1e1e1e', text:'#fff', sub:'#aaa', border:'#333', prim:'#bb86fc'} : {bg:'#f8', card:'#fff', text:'#000', sub:'#666', border:'#ddd', prim:'#6200ee'};
  if (isLocked) return (<SafeAreaView style={{flex:1, backgroundColor:theme.bg, justifyContent:'center', alignItems:'center', padding:20}}><Text style={{color:theme.text, fontSize:28, fontWeight:'bold', marginBottom:20}}>🔐 GEMBOK</Text><TextInput style={{width:'100%', height:50, backgroundColor:theme.card, color:theme.text, borderWidth:1, borderColor:theme.border, borderRadius:8, paddingHorizontal:16, marginBottom:16}} placeholder='Master PIN' placeholderTextColor={theme.sub} secureTextEntry keyboardType='numeric' value={inputPin} onChangeText={setInputPin}/><TouchableOpacity style={{width:'100%', height:50, backgroundColor:theme.prim, borderRadius:8, justifyContent:'center', alignItems:'center'}} onPress={handleUnlock}><Text style={{color:'#fff', fontWeight:'bold'}}>Buka</Text></TouchableOpacity></SafeAreaView>);
  return (<SafeAreaView style={{flex:1, backgroundColor:theme.bg, padding:16}}><View style={{flexDirection:'row', justifyContent:'space-between', alignItems:'center', marginBottom:16}}><Text style={{color:theme.text, fontSize:22, fontWeight:'bold'}}>🔐 GEMBOK</Text><View style={{flexDirection:'row', gap:10}}><TouchableOpacity onPress={()=>setIsDarkMode(!isDarkMode)}><Text style={{fontSize:20}}>{isDarkMode?'☀️':'🌙'}</Text></TouchableOpacity><TouchableOpacity onPress={()=>setIsLocked(true)}><Text style={{fontSize:20}}>🔒</Text></TouchableOpacity></View></View>
  {currentScreen==='home' ? (<View style={{flex:1}}><TextInput style={{height:45, backgroundColor:theme.card, color:theme.text, borderWidth:1, borderColor:theme.border, borderRadius:8, paddingHorizontal:16, marginBottom:12}} placeholder='Cari...' placeholderTextColor={theme.sub} value={searchQuery} onChangeText={setSearchQuery}/><FlatList data={filtered} keyExtractor={i=>i.id} renderItem={({item})=>(<View style={{backgroundColor:theme.card, padding:16, borderRadius:8, borderWidth:1, borderColor:theme.border, marginBottom:10, flexDirection:'row', justifyContent:'space-between', alignItems:'center'}}><View><Text style={{color:theme.text, fontSize:16, fontWeight:'bold'}}>{item.title}</Text><Text style={{color:theme.sub}}>{item.username}</Text></View><View style={{flexDirection:'row', gap:8}}><TouchableOpacity style={{backgroundColor:theme.prim, padding:8, borderRadius:6}} onPress={()=>copyPassword(item.password)}><Text style={{color:'#fff', fontSize:12}}>Salin</Text></TouchableOpacity><TouchableOpacity style={{backgroundColor:'#ff4d4d', padding:8, borderRadius:6}} onPress={()=>deleteItem(item.id)}><Text style={{color:'#fff', fontSize:12}}>Hapus</Text></TouchableOpacity></View></View>)}/><TouchableOpacity style={{position:'absolute', right:16, bottom:16, width:56, height:56, borderRadius:28, backgroundColor:theme.prim, justifyContent:'center', alignItems:'center'}} onPress={()=>setCurrentScreen('add')}><Text style={{color:'#fff', fontSize:28}}>+</Text></TouchableOpacity></View>) : (<View><Text style={{color:theme.text, fontSize:18, fontWeight:'bold', marginBottom:16}}>Tambah Password</Text><TextInput style={{height:50, backgroundColor:theme.card, color:theme.text, borderWidth:1, borderColor:theme.border, borderRadius:8, paddingHorizontal:16, marginBottom:12}} placeholder='Judul' placeholderTextColor={theme.sub} value={title} onChangeText={setTitle}/><TextInput style={{height:50, backgroundColor:theme.card, color:theme.text, borderWidth:1, borderColor:theme.border, borderRadius:8, paddingHorizontal:16, marginBottom:12}} placeholder='Username' placeholderTextColor={theme.sub} value={username} onChangeText={setUsername}/><TextInput style={{height:50, backgroundColor:theme.card, color:theme.text, borderWidth:1, borderColor:theme.border, borderRadius:8, paddingHorizontal:16, marginBottom:12}} placeholder='Password' placeholderTextColor={theme.sub} secureTextEntry value={password} onChangeText={setPassword}/>{password ? <Text style={{color:str.c, marginBottom:12}}>Kekuatan: {str.t}</Text> : null}<TouchableOpacity style={{height:50, backgroundColor:theme.prim, borderRadius:8, justifyContent:'center', alignItems:'center', marginBottom:10}} onPress={savePasswordItem}><Text style={{color:'#fff', fontWeight:'bold'}}>Simpan</Text></TouchableOpacity><TouchableOpacity style={{height:50, borderWidth:1, borderColor:theme.border, borderRadius:8, justifyContent:'center', alignItems:'center'}} onPress={()=>setCurrentScreen('home')}><Text style={{color:theme.text}}>Batal</Text></TouchableOpacity></View>)}</SafeAreaView>); }