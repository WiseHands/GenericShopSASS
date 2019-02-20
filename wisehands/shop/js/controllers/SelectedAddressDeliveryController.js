// selectedcourierdelivery
(function(){
    angular.module('WiseShop')
        .controller('SelectedAddressDeliveryController', ['$scope', '$http', '$route', 'shared', '$route', '$location',
            function($scope, $http, shared, $route, $location) {


               $http({
                   method: 'GET',
                   url: '/delivery'
               }).then(function successCallback(response) {
                       $scope.deliverance = response.data;
                       $scope.minOrderForFreeDelivery = $scope.deliverance.courierFreeDeliveryLimit;

                   }, function errorCallback(error) {
                       console.log(error);
                   });


               $scope.customerData = function () {
                   if (!$scope.place) {
                       return;
                   }
                   if ($scope.place && $scope.place.formatted_address){
                       localStorage.setItem('address', $scope.place.formatted_address);
                       localStorage.setItem('addressLat', $scope.place.geometry.location.lat());
                       localStorage.setItem('addressLng', $scope.place.geometry.location.lng());
                   }
                   if (!$scope.place.formatted_address) {
                       localStorage.setItem('addressLat', '');
                       localStorage.setItem('addressLng', '');
                   }

               };

                $scope.goToRoute = function() {
                  location.hash = '#!/selectedcourierdelivery'
                }

                $http({
                    method: 'GET',
                    url: '/courier/polygon'
                })
                    .then(function successCallback(response) {
                        $scope.courierPolygonData = JSON.parse(response.data);
                        // var data = $scope.courierPolygonData;
                        arrayCoordinates = $scope.courierPolygonData.features[0].geometry.coordinates[0];
                        console.log("loadPolygons response", response, typeof arrayCoordinates, arrayCoordinates);
                    }, function errorCallback(data) {
                        $scope.status = 'Щось пішло не так... з координатами ';
                    });

                  $http({
                      method: 'GET',
                      url: '/contact/details'
                  })
                      .then(function successCallback(response) {
                          $scope.contacts = response.data;
                          init_map($scope.contacts.latLng);
                          console.log($scope.contacts.latLng);
                      }, function errorCallback(data) {
                          $scope.status = 'Щось пішло не так...';
                      });

                      var map;
                      var marker;
                      var latlng;
                      var polygon;
                      var geocoder;
                      var arrayCoordinates;
                      function init_map(latLng) {
                          geocoder = new google.maps.Geocoder();
                          if (!latLng) return;

                          var cords = latLng.split(':');
                          var lat = cords[0];
                          var lng = cords[1];
                          var var_location = new google.maps.LatLng(lat, lng);

                          var var_map_options = {
                              center: var_location,
                              zoom: 16,

                              };
                          // set googleMap By Id
                          // marker = new google.maps.Marker({
                          //   position: var_location,
                          //   map: map,
                          //   visible: false
                          // });
                          map = new google.maps.Map(document.getElementById("googleMap"), var_map_options);
                          polygonMap();



                            google.maps.event.addListener(map, 'click', function(event) {
                              if( marker ) marker.setMap( null );

                              marker = new google.maps.Marker({
                                    map: map,
                                    position: event.latLng,
                                  });
                              console.log(google.maps.geometry.poly.containsLocation(event.latLng, polygon));
                              latlng = new google.maps.LatLng(event.latLng.lat(), event.latLng.lng());;
                              geocodeLatLng(latLng);
                            });



                        // Apply listeners to refresh the GeoJson display on a given data layer.

                        google.maps.event.addListener(polygon, 'click', function(event) {
                          if( marker ) marker.setMap( null );

                          marker = new google.maps.Marker({
                                map: map,
                                position: event.latLng,
                              });
                          console.log(google.maps.geometry.poly.containsLocation(event.latLng, polygon));
                        });

                      }

                        function polygonMap(){
                          var objectCoordinates = [];
                          for (var i = 0; i < arrayCoordinates.length; i++) {
                            objectCoordinates.push({
                              lat: arrayCoordinates[i][1],
                              lng: arrayCoordinates[i][0]
                            });
                          };
                          console.log('objectArray', objectCoordinates);


                            var polygonOptions = {
                                paths: objectCoordinates,
                                clickable: true,
                                visible: true,
                                draggable: true,
                                strokeColor: '#FF0000',
                                strokeOpacity: 0.8,
                                strokeWeight: 2,
                                fillColor: '#FF0000',
                                fillOpacity: 0.35
                              };

                          polygon = new google.maps.Polygon(polygonOptions);
                          polygon.setMap(map);
                        }

                        function geocodeLatLng(latLng) {
                          geocoder.geocode({'location': latlng}, function(results, status) {
                            if (status === 'OK') {
                              if (results[0]) {
                                map.setZoom(16);
                                // $scope.place = results[0].formatted_address;
                                $scope.$apply(function () {
                                      $scope.place = results[0].formatted_address;
                                      localStorage.setItem('address', $scope.place);
                                  });
                                console.log('address', results[0].formatted_address, $scope.place);
                                var marker = new google.maps.Marker({
                                  position: latlng,
                                  map: map
                                });
                              } else {
                                console.log('no address');
                              }
                            } else {
                              console.log('finded address ', status);
                            }
                          });
                        }

                          $scope.codeAddress = function() {
                          var address = document.getElementById('address').value;
                          console.log(address);
                          geocoder.geocode( { 'address': address}, function(results, status) {
                            if (status == 'OK') {
                              map.setCenter(results[0].geometry.location);
                              if (marker && marker.setMap) {
                                    marker.setMap(null);
                                }
                              marker = new google.maps.Marker({
                                  map: map,
                                  position: results[0].geometry.location
                              });
                            } else {
                              alert('Geocode was not successful for the following reason: ' + status);
                            }
                          });
                        }


            }]);
})();
function encodeQueryData(data)
{
    var ret = [];
    for (var d in data)
        ret.push(encodeURIComponent(d) + "=" + encodeURIComponent(data[d]));
    return ret.join("&");
}
