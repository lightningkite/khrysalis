package com.lightningkite.kwift.flow

import com.lightningkite.kwift.swift.TabWriter
import org.junit.Test

class CodeSectionTest {
    val text = """
//
// ClientSessionVG.swift
// Created by Kwift Prototype Generator
// Any changes made to this file will be overridden unless this comment is removed.
//
package com.klypme.shared.vg

//--- Imports
import android.widget.*
import android.view.*
import com.lightningkite.kwift.actual.*
import com.lightningkite.kwift.shared.*
import com.lightningkite.kwift.views.actual.*
import com.lightningkite.kwift.views.shared.*
import com.lightningkite.kwift.observables.actual.*
import com.lightningkite.kwift.observables.shared.*
import com.klypme.R
import com.klypme.layouts.*

//--- VG Name
@Suppress("NAME_SHADOWING")
class ClientSessionVG(
    //--- Dependencies
    @unowned val stack: ObservableStack<ViewGenerator>,
    @unowned val root: ObservableStack<ViewGenerator>,
    @unowned val dialog: ObservableStack<ViewGenerator>
    //--- VG Extends
) : ViewGenerator() {
    //--- Body Start
    
    //--- define clientSession
    val clientSession: ObservableStack<ViewGenerator> = ObservableStack()
    //--- define other
    val myCustomVariable = false
    
    //--- Title
    override val title: String get() = "ClientSession"
    
    //--- Generate Start
    override fun generate(dependency: ViewDependency): View {
        val xml = ClientSessionXml()
        val view = xml.setup(dependency)
        
        //--- Set up content
        xml.content.bindStack(dependency, clientSession)
        
        //--- Set up find
        xml.find.onClick(captureWeak(this){ self -> self.clientSession.reset(SearchFrontVG(stack = this.clientSession, root = this.root, dialog = this.dialog)) })
        
        //--- Set up appointments
        xml.appointments.onClick(captureWeak(this){ self -> self.clientSession.reset(ClientAppointmentsVG(stack = this.clientSession, root = this.root, dialog = this.dialog)) })
        
        //--- Set up messages
        xml.messages.onClick(captureWeak(this){ self -> self.clientSession.reset(MessagesListVG(stack = this.clientSession)) })
        
        //--- Set up newMessageCount
        xml.newMessageCount.textString = "2"
        
        //--- Set up profile
        xml.profile.onClick(captureWeak(this){ self -> self.clientSession.reset(ClientProfileEditableVG(stack = this.clientSession, dialog = this.dialog, root = this.root)) })
        
        //--- Set up more
        xml.more.onClick(captureWeak(this){ self -> self.clientSession.reset(ClientMoreVG(stack = this.clientSession, root = this.root, dialog = this.dialog, clientSession = this.clientSession)) })

        //--- Startup Logic
        
        //--- Generate End
        return view
    }
    
    //--- init start
    init {
        //--- init clientSession
        this.clientSession.reset(SearchFrontVG(stack = this.clientSession, root = this.root, dialog = this.dialog))
        //--- init end
    }
    
    //--- View Functions
    
    fun example(){
    }
    
    //--- Body End
}
        """

    @Test fun testSameOutput(){

        val sections = CodeSection.read(text.lines())

        val output = buildString {
            with(TabWriter(this)){
                for(section in sections){
                    section.writeWhole(this)
                }
            }
        }

        text.lineSequence().zip(output.lineSequence()).forEach {
            if(it.first.trim() != it.second.trim()) {
                throw Exception("OFFSET: $it")
            }
            if(it.first.asSequence().takeWhile { it == ' ' }.count() != it.second.asSequence().takeWhile { it == ' ' }.count()) {
                throw Exception("TAB MISMATCH: $it")
            }
        }
    }

    @Test fun merge() {
        val user = CodeSection.read("""
            //--- A (overwritten on flow generation)
            a()
            
            //--- B
            b()
            bMore()
            
            //--- C (overwritten on flow generation)
            c()
            
        """.lines())
        val gen = CodeSection.read("""
            //--- A (overwritten on flow generation)
            a(1)
            
            //--- B (overwritten on flow generation)
            b(2)
            
            //--- C (overwritten on flow generation)
            c(3)
            
        """.lines())
        ///Triple
        val sections = user.mergeOverride(gen)

        val output = buildString {
            with(TabWriter(this)){
                for(section in sections){
                    section.writeWhole(this)
                }
            }
        }
        println(output)
    }
}
