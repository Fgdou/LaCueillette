let user = null
let page = null
let lastPage = "search"
let token = null
let url = "https://lacueillette.ml/v2/"
let api = "https://lacueillette.ml/api/"

$(()=>{
    openWindow(lastPage)

    $("header .account").click(()=>{
        if(user === null)
            openWindow("login")
    })
    $("header .logo").click(()=>{
        window.location.replace(url)
    })
    $(".window.login .submit button").click(()=>{
        let bt = $(".window.login .submit")

        clearInputError()

        let email = $(".window.login .email input").val()
        let password = $(".window.login .password input").val()

        if(email === "")
            inputError("email")
        else if(password === "")
            inputError("password")
        else{
            bt.prop("disabled", true);

            $.post(api + "user/login", {
                email: email,
                password: password
            }, (data)=>{
                bt.prop("disabled", false);
                if(data.error){
                    if(data.error === "Wrong password")
                        inputError("password")
                    else if(data.error === "User not found")
                        inputError("email")
                    else
                        errorPopup(data.error)
                }else{
                    token = data.value
                    user = data.user
                    login(user)
                    openWindow(lastPage)
                    document.cookie = "token="+data.value + "; expires="+dateToString(data.expiration)
                }
            },"json");
        }
    })
    $(".login").submit(e => e.preventDefault())

    let cookie = getCookie("token")
    if(cookie !== ""){
        $.post(api + "user/", {
            user_token: cookie
        }, (data)=>{
            if(data.error){
                errorPopup(data.error)
                document.cookie = "token=; expires=Thu, 1 Jan 1970 00:00:01 UTC"
            }else{
                user = data
                token = cookie
                login(user)
            }
        }, "json")
    }


})
function login(user){
    $("header .account span").text("Bonjour, " + user.name.charAt(0).toUpperCase() + user.name.slice(1))
}
function logout(){
    $.post(api+"user/logout", {
        user_token: token
    }, null, "json")
    $("header .account span").text("Mon compte")
    document.cookie = "token=; expires=Thu, 1 Jan 1970 00:00:01 UTC"
}
function openWindow(name){
    closeWindows()
    if(name != page)
        lastPage = page
    page = name
    $(".window."+name).css("display", "grid")
}
function closeWindows(){
    $(".window").css("display", "none")
}
function inputError(name){
    $(".input."+name).addClass("error")
    $(".input."+name + " input").focus()
}
function clearInputError(){
    $(".input").removeClass("error")
}
function errorPopup(msg){
    console.log(msg)
}
function dateToString(date){
    return (new Date(date.year, date.month, date.day+1, date.hour, date.minute, date.second)).toString()
}
function getCookie(cookiename) {
    var name = cookiename + "=";

    // Decode URI to normal if needed and split into an array of cookies
    var arrayOfCookies = decodeURIComponent(document.cookie).split(';');

    // examine all elements in the cookie array (list)
    for (var i = 0; i < arrayOfCookies.length; i++) {
        // Get the next cookie from the array
        var aCookie = arrayOfCookies[i];
        // Skip prefix spaces
        while (aCookie.charAt(0) == ' ') {
            aCookie = aCookie.substring(1);
        }
        // if the cookie name is found, return the value part of the string
        if (aCookie.indexOf(name) == 0) {
            return aCookie.substring(name.length, aCookie.length);
        }
    }
    // return nothing if we didn't find the cookie name
    return "";
}