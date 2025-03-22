package com.pojo.droptruck.di


import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class,])
interface AppComponent {


}