angular.module('WiseHands')
    .controller('NetworkShopListController', ['$scope', '$http', '$routeParams', 'sideNavInit', function ($scope, $http, $routeParams, sideNavInit) {
        $scope.loading = true;
        $scope.uuid = $routeParams.uuid;

        sideNavInit.sideNav();

        $http({
            method: 'GET',
            url: '/shop-network/' + $routeParams.uuid
        })
            .then(function successCallback(response){
                $scope.networkShopList = response.data.shopList;
                $scope.networkName = response.data.networkName;
                $scope.uuid = response.data.uuid;
                console.log("in response network details /shop-network/uuid ", $scope.uuid, $scope.networkShopList,response);
            }, function errorCallback(data){
        });


        $http({
            method: 'GET',
            url: '/shop/details/public'
        })
            .then(function successCallback(response){
                $scope.activeShop = response.data;

            }, function errorCallback(data){
        });

        $scope.getLat = function (shop) {
            var cords = shop.contact.latLng.split(',');
            let lat = cords[0];
            return lat;
        };

        $scope.getLng = function (shop) {
            var cords = shop.contact.latLng.split(',');
            let lng = cords[1];
            return lng;
        };


    }]);
