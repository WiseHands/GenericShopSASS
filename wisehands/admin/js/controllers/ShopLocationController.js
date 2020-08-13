angular.module('WiseHands')
  .controller('ShopLocationController', ['$scope', '$http', function ($scope, $http) {

    $scope.goBack = () => {
      window.history.back();
    };


    $http({
      method: 'GET',
      url: '/contact/details'
    })
      .then(response => {
      $scope.contacts = response.data;
      const shopLocation = response.data.shopLocation;
      console.log("setAddressFromResponse", $scope.contacts);
      initMap(shopLocation);
    }, error => {
      $scope.status = 'Щось пішло не так...';
      console.log(error);
    });

    initMap = (shopCoordinates) => {
      let latitude = Number(shopCoordinates.latitude);
      let longitude = Number(shopCoordinates.longitude);
      if ((!!latitude && !!longitude) || (latitude === 0 && longitude === 0)){
         latitude = 49.8433513;
         longitude =24.0315123;
      }
      let shopLocation = new google.maps.LatLng(latitude, longitude);
      let mapOptions = {
         streetViewControl: false,
         center: shopLocation,
         zoom: 17
      };
      let map = new google.maps.Map(document.getElementById('googleMap'), mapOptions);
      let marker = new google.maps.Marker({position: shopLocation, map: map});
      map.addListener('click', function(event) {
          $scope.pointLocation = event.latLng
          if (marker) marker.setMap(null);
          marker = new google.maps.Marker(
            { position: $scope.pointLocation,
               map: map }
            );
          geocodePointLocation($scope.pointLocation);
          console.log('click on map', event.latLng);
        });
    }

    geocodePointLocation = (location) => {
      let geocoder = new google.maps.Geocoder();
      geocoder.geocode({
        'location': location
        }, function(results, status) {
          if (status === 'OK') {
              if (results[0]) {
                  addressCity.value = results[0].address_components[2].long_name;
                  addressStreet.value = results[0].address_components[1].long_name;
                  addressNumberHouse.value = results[0].address_components[0].long_name;
              } else {
                    console.log('no address');
              }
            } else {
                console.log('finded address ', status);
            }
        });
    }

    $scope.saveShopLocation = () => {
      console.log('pointLocation from map', $scope.pointLocation.lat(), $scope.pointLocation.lng());
      console.log('pointLocation from map', addressCity.value, addressStreet.value,addressNumberHouse.value);
      objectBody = {
        addressCity: addressCity.value,
        addressStreet: addressStreet.value,
        addressNumberHouse: addressNumberHouse.value,
        shopLocation: {
          latitude: $scope.pointLocation.lat(),
          longitude: $scope.pointLocation.lng()
        }
      };
      console.log('objectBody for update contacts', objectBody);
      _saveNewAddress(objectBody);
    };

    _saveNewAddress = (objectBody) => {
      $http({
        method: 'PUT',
        url: '/contact',
        data: objectBody,
      }).then(_showSaveToaster(),
        error => {
          $scope.status = 'Щось пішло не так...';
          console.log(error);
        });
    }

    _showSaveToaster = () => {
      toastr.clear();
      toastr.options = {
        'positionClass': 'toast-bottom-right',
        'preventDuplicates': true,
      };
      toastr.info('Saved successfully');
    }


  }]);
