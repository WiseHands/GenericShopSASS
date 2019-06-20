angular.module('WiseHands')
    .controller('SideNavController', ['$scope', '$http', '$route', 'signout', '$window', 'shared',
    			function ($scope, $http, $route, signout, $window) {
        $scope.$route = $route;


        $http({
            method: 'GET',
            url: '/shop/details',
        })
            .then(function successCallback(response) {
                $scope.activeShop = response.data;
                localStorage.setItem('activeShop', $scope.activeShop.uuid);
                localStorage.setItem('activeShopName', $scope.activeShop.shopName);
                console.log("/shop/details", response);
            }, function errorCallback(response) {
            });

        $http({
            method: 'GET',
            url: '/network',
        })
            .then(function successCallback(response) {
                console.log("network", response);
                $scope.networkUuid = response.data.uuid;
                if (response.data != null){
                    $scope.networkName = response.data.networkName;
                } else {
                    $scope.networkName = null;
                }
                }, function errorCallback(reason) {
                }
            );

        $scope.getNetwork = function () {
            $window.location.href = '/admin#/networkshoplist/' + $scope.networkUuid;

        };

        $scope.$watch(function () {
            if (!$scope.activeShop) {
                return;
            }
            $scope.activeShop.shopName = localStorage.getItem('activeShopName');


        });

        $scope.getUrl = function () {
            $window.location.href = window.location.protocol + '//' + $scope.activeShop.domain + ':' + window.location.port;
        };
        $scope.signOut = signout.signOut;


    }]);
