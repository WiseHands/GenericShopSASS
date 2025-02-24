angular.module('WiseHands')
    .controller('AddPropertyController', ['$scope', '$http', 'signout', '$routeParams', '$location', function ($scope, $http, signout, $routeParams, $location) {
        $scope.loading = false;
        $scope.productUuid = $routeParams.productUuid;
        $scope.categoryUuid = $routeParams.categoryUuid;

        var locale = localStorage.getItem('locale');

        $http({
            method: 'GET',
            url: '/api/product/' + $scope.productUuid
        })
            .then(function successCallback(response) {
                console.log("response for product ", response.data);
                $scope.product = response.data;
                $scope.activeShop = localStorage.getItem('activeShop');
                $scope.product.images.forEach(function(image, index){
                    if(image.uuid === $scope.product.mainImage.uuid){
                        $scope.selected = index;
                    }
                })
            }, function errorCallback(error) {

            });

        $scope.uploadOptionImage = function () {
            $('#imageLoader').click();

        };

        var imageLoader = document.getElementById('imageLoader');
        imageLoader.addEventListener('change', handleImage, false);

        function handleImage(e){
            var file  = e.target.files[0];
            var reader = new FileReader();
            reader.onloadend = function(event){

                const propertyImage = document.querySelector("#property_img");
                propertyImage.setAttribute('src', event.target.result);

            };
            if (file && file.type.match('image.*')) {
                reader.readAsDataURL(e.target.files[0]);
            }
        }

        $scope.createProperty = function () {
            if (!document.getElementById("imageLoader").value) {
                document.querySelector(".error-text").style.display = "block";
                return;
            }
            if (!$scope.addition) {
                toastr.error(emptyTagWarning);
            } else {
                const photo = document.getElementById("imageLoader").files[0];
                $scope.loading = true;
                let photoFd = new FormData();
                photoFd.append('logo', photo);
                $http.post('/upload-file', photoFd, {
                    transformRequest: angular.identity,
                    headers: {
                        'Content-Type': undefined,
                    }
                })
                    .success(function(response){
                        $scope.loading = false;
                        $scope.addition.filepath = response.filepath;
                        sendAdditionProperty();
                    })
                    .error(function(response){
                        $scope.loading = false;
                        console.log(response);
                });

            }
        };
        function sendAdditionProperty(){
            $http({
                method: 'POST',
                url: '/addition/' + $scope.productUuid,
                data: $scope.addition
            })
                .then(function successCallback(response) {
                    $location.path('/products/details/' + $scope.productUuid + '/edit');
                    console.log("$scope.addition", response.data);
                    $scope.loading = false;
                }, function errorCallback(response) {
                    $scope.loading = false;
                    console.log("$scope.addition", response);
                });
        }

    }]);




