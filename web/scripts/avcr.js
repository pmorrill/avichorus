/**
 * Javascript to support the Point Count Recording analysis servlets and webpages
 * 
 * @type type
 */
var stopIcon = {
    path: google.maps.SymbolPath.CIRCLE,
    fillColor: '#FF6666',
    fillOpacity: .9,
    scale: 6.5,
    strokeColor: '#000000',
    strokeWeight: 1
};
var iWindow = null;
var g_map = null;

/**
 * Use Google maps to display a map of the recordings in the project
 * 
 * @param {type} data JSON encoded data structure
 * @returns {undefined}
 */
function map(data) {
  $div = document.getElementById('recordingMap');
  if ( $div.length == 0 ) return;
  var mt = google.maps.MapTypeId.TERRAIN;
  var long_lat = new google.maps.LatLng(data.centery,data.centerx);
  var myOptions = {
    zoom: data.zoom,
    center: long_lat,
    disableDefaultUI: false,
    mapTypeId: mt
  };
  var g_map = new google.maps.Map($div, myOptions);
  var g_markers = [];
  for (var i = 0; i < data.markers.length; i++) {
    mIcon = stopIcon;
    myLatLng = new google.maps.LatLng(data.markers[i]["latitude"], data.markers[i]["longitude"]);
    var dot = new google.maps.Marker({
      position: myLatLng,
      icon: mIcon,
      map: data.cluster ? null : g_map,
      title: data.markers[i]["recording"],
      shadow: null
    });

    g_markers[i] = dot;
    var info = "<div class=\"marker-info\"><strong>" + data.markers[i].longitude + ", " + data.markers[i].latitude + "<br />" +
            data.markers[i].habitat + "</strong><br /><br />" + data.markers[i].siteName + " (" + data.markers[i].province + ")<br />"
            + data.markers[i].recording;
    info += '&nbsp;&nbsp;<a href="/avcr-abmi/demo/play?id=' + data.markers[i].recordingId + '">Analyse</a><br /></div>'
    g_markers[i].info = info;
    g_markers[i].addListener("click", function () {
      if (iWindow) iWindow.close();
      iWindow = new google.maps.InfoWindow({content: this.info});
      iWindow.open(g_map, this);
    });
  }

  g_map.addListener("click",function() { if ( iWindow ) { iWindow.close(); iWindow = null; } });
}

function init_player6(silent,play_url,d_id,bAutoStart,cb_scroll,cb_onseek,cb_onplay) {
	gPlayerId = d_id;
	if ( !play_url ) { play_url = 'avichorus/recordings/silence.mp3'; autoStart = 0; }
	jwplayer(d_id).setup({
        	file: play_url,
        	height: 24,width: 310,
		title: '',autostart: false,
		primary: 'flash',repeat: false
	});
	jwplayer(d_id).setVolume(50);
	if ( !silent && bAutoStart ) jwplayer(d_id).play(true);
	if ( cb_onseek ) jwplayer(d_id).onSeek(cb_onseek);
	if ( cb_onplay ) jwplayer(d_id).onPlay(cb_onplay);
	if ( cb_scroll ) jwplayer(d_id).onTime(cb_scroll);
	gPlayerVersion = 6;
}

var g_media = null;
var g_player = 'pl_twrap';
var g_media = null; // file to play


$(function() {
	$('#spectrogram-wrap').css('cursor', 'wait'); 
	if ( g_media != null ) init_player6(false,g_media,g_player,true,null,null,null);
//	if ( g_media != null ) init_player6(0,g_media,g_player,0,avcr_jwp_cb_spectro_scroll,avcr_jwp_cb_spectro_seek,avcr_jwp_cb_spectro_play);
    $('#spectrogram-wrap').css('cursor', 'default'); 
});