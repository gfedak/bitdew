
    /*! @mainpage BitDew: An Open Source Middleware for Large Scale Data Management
     *
     * This is a short introduction to run the example in the BitDew
     * source package. To now more about BitDew, its principles and
     * architecture and what can it be used for,  read the \ref introduction. 
     *
     * @section buildsec Downloading and Building
     *
     * Please Download BitDew from the BitDew web site
     * http://www.BitDew.net

     *
     * To install the BitDew software follow the steps :
     * <ol>
     *  <li> deflate the BitDew archive</li>
     *  <li> move in the BitDew directory</li>
     * </ol>
     * 
     * If you modify the source and if you need to compile again
     * BitDew, run the following command :
     @code
     ./build.sh
     @endcode
     * The option to build every packages, including the stand-alone
     * jar file is :
     @code
     ./build.sh release
     @endcode

     * You can look at all the available option for the build command
     * in the module \ref build

     @section runsec Running the tool

     * <ol>
     * <li>start a server. Open a terminal window and start the
     * following command</li>

     @code
     java -jar bitdew-stand-alone.jar serv dc dt dr ds
     @endcode
     
     *to get everything about the command line utilities, please
     *consult \ref cmdline
     
     * </ol>

     @section exsec Running the examples
     *  
     * Examples have similar structure : the example code is usually a
     * client. You will need to execte the different services to be
     * able to start the exemples
     * <ol>
     * <li>start a server. Open a terminal window and start the
     * following command</li>

     @code
     java -jar bitdew-stand-alone.jar serv dc dt dr ds
     @endcode

     * <li> start the example

     @code
     java -cp bitdew-stand-alone.jar xtremweb.role.examples.HelloWorld localhost
     @endcode
     * 
     *<li> Please, consult the documentation specific for each
     * example here \ref examples !
     </ol>
     */

/*!
 * @defgroup introduction Introduction
 *  @{

    @section what What is BitDew ?

    The BitDew framework is a programmable environment for management and distribution of data on computational Desktop Grids.

    BitDew is a subsystem which can be easily integrated into Desktop Grid systems (XtremWeb, BOINC, Condor etc..). Currently, Desktop Grids are mostly limited to embarrassingly parallel applications with few data dependencies. BitDew objective is to broaden the use of Desktop Grids. Our approach is to break the "data wall" by providing in single package the key P2P technologies (DHT, BitTorrent) and high level programming interfaces. We first target Desktop Grid with peta-scale data system : up to 1K files/nodes, with size up to 1GB and distributed to 10K to 100K nodes.

    The BitDew framework will enable the support for data-intense parameter sweep applications, long-running applications which requires distributed checkpoint services, workflow applications and maybe in the future soft-realtime and stream processing applications.

    @section can What Can I do with BitDew ?

    BitDew offers programmers a simple API for creating, accessing, storing and moving data with ease, even on highly dynamic and volatile environments.

    The BitDew programming model relies on 5 abstractions to manage the data : i) replication indicates how many occurrences of a data should be available at the same time on the network, ii) fault-tolerance controls the policy in presence of machine crash, iii) lifetime is an attribute absolute or relative to the existence of other data, which decides the life cycle of a data in the system, iv) affinity drives movement of data according to dependency rules, v) protocol gives the runtime environment hints about the protocol to distribute the data (http, ftp or bittorrent). Programmers define for every data these simple criteria, and let the BitDew runtime environment manage operations of data creation, deletion, movement, replication, and fault-tolerance operation.

    @section architecture Bitdew Architecture

    The BitDew runtime environment is a flexible environment implementing the API. It relies both on centralized and distributed protocols for indexing, storage and transfers providing reliability, scalability and high-performance.

    The architecture follows a classical three-tiers schema commonly found in Desktop Grids: it divides the world in two sets of nodes : stable nodes and volatile nodes. Stable nodes run various independent services which compose the runtime environment: Data Repository (DR), Data Catalog (DC), Data Transfer (DT) and Data Scheduler (DC). We call these nodes the service hosts. Volatile nodes can either ask for storage resources (we call them client hosts) or offer their local storage (they are called reservoir hosts). Usually, programmers will not use directly the various D* services; instead they will use the API which in turn hides the complexity of internal protocols. 

    The Bitdew runtime environment delegates a large number of operation to third party components : 1) Meta-data information are serialized using a traditional SQL database, 2) data transfer are realized out-of-band by specialized file transfer protocols and 3) publish and look-up of data replica is enabled by the means of of DHT protocols. One feature of the system is that all of these components can be replaced and plugged-in by the users, allowing them to select the most adequate subsystem according to their own criteria like performance, reliability and scalability.
@}
*/

    /*!  @defgroup build Downloading and Compiling BitDew
     * 
     *  @{

     *     @section downsec Downloading BitDew

The BitDew files are downloadable from http://www.bitdew.net :

     @li @c bitdew-stand-alone-X.Y.Z.jar
contains everything you need to run the software.
     @li @c bitdew-src-X.Y.Z.jar
contains the sources of BitDew as well as librairies needed to compile BitDew.
     @item @c bitdew-lib-X.Y.Z.jar 
contains the BitDew classes.


@section compile Compiling BitDew

@subsection compile1 The Short Way

Deflate the distribution with the command :
@code
unzip bitdew-src-X.Y.Z.zip
@endcode


You don't need to compile BitDew, unless you have modified the sources. 
To compile BitDew, move in the BitDew directory and execute the following commande : 

@code
  build.sh
@endcode

First, the command preprocesses @c .idl files, from which it generates @c .java. Next, it invokes the java compiler and places @c .class files in the @c lib directory.

The following command generates everything you need :
@code
build.sh release
@endcode

@subsection advusage Advanced Usage

When developping BitDew, you may need to use the following build targets as well :


     @li  @code build.sh clean @endcode
     Delete files generated by the build process.
     @li  @code build.sh tests @endcode
     Run the Junit unitary tests.
     @li  @code build.sh tests-reports @endcode
     Same as  @c build.sh @c tests, but results of unitary tests are placed in the @c reports directory. 
     @li  @code build.sh javadoc @endcode
     Build the javadoc for the various APIs.
     @li  @code build.sh srcdoc @endcode
     Build the full source code documentation. This targets depends on the Doxygen tool. It also requires time, processing power and disk space.
     @li  @code build.sh jar @endcode
     Create a new @c bitdew-lib-X-Y-Z.jar file.
     @li  @code build.sh stand-alone-jar @endcode
     Create a new @c bitdew-stand-alone-X.Y.Z.jar file.
     @li  @code build.sh release @endcode
     Make a new BitDew release, that is, run most of the previous targets.

     * @}
     */




